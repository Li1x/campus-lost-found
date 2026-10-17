package com.campus.lostfound.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.lostfound.common.ResultCode;
import com.campus.lostfound.common.enums.UserRole;
import com.campus.lostfound.common.exception.BusinessException;
import com.campus.lostfound.dto.ChangePasswordRequest;
import com.campus.lostfound.dto.LoginRequest;
import com.campus.lostfound.dto.RefreshTokenRequest;
import com.campus.lostfound.dto.RegisterRequest;
import com.campus.lostfound.dto.UpdateProfileRequest;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.mapper.UserMapper;
import com.campus.lostfound.security.JwtUtil;
import com.campus.lostfound.service.UserService;
import com.campus.lostfound.vo.LoginResponse;
import com.campus.lostfound.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${business.auth.max-login-attempts:5}")
    private int maxLoginAttempts;

    @Value("${business.auth.lock-duration-minutes:15}")
    private int lockDurationMinutes;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoVO register(RegisterRequest request) {
        // 1. 检查学号是否已被使用
        long countByStudentId = count(new LambdaQueryWrapper<User>()
                .eq(User::getStudentId, request.getStudentId()));
        if (countByStudentId > 0) {
            throw BusinessException.conflict("该学号已被注册");
        }

        // 2. 检查用户名是否已被使用
        long countByUsername = count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (countByUsername > 0) {
            throw BusinessException.conflict("该用户名已被占用");
        }

        // 3. 构造并保存用户实体
        User user = new User();
        user.setStudentId(request.getStudentId());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setContactInfo(request.getContactInfo());
        user.setRole(UserRole.STUDENT.getCode()); // 默认注册角色为学生
        user.setStatus(1); // 默认正常
        user.setLoginFailCount(0);

        save(user);
        log.info("用户注册成功: id={}, username={}", user.getId(), user.getUsername());

        return toUserInfoVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse login(LoginRequest request) {
        // 1. 根据用户名或学号查找用户
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername())
                .or()
                .eq(User::getStudentId, request.getUsername()));

        if (user == null) {
            throw BusinessException.of(ResultCode.UNAUTHORIZED, "用户名或密码错误");
        }

        // 2. 检查账号是否处于禁用状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw BusinessException.forbidden("该账号已被管理员禁用，请联系管理员");
        }

        // 3. 检查账号是否处于锁定状态
        LocalDateTime now = LocalDateTime.now();
        if (user.getLockTime() != null && user.getLockTime().isAfter(now)) {
            long remainingMinutes = Duration.between(now, user.getLockTime()).toMinutes() + 1;
            throw BusinessException.of(ResultCode.TOO_MANY_REQUESTS,
                    String.format("账号已被暂时锁定，请于 %d 分钟后重试", remainingMinutes));
        }

        // 4. 校验密码（优先 BCrypt 校验，若数据库为手动填写的明文则平滑兼容并自动加密升级）
        boolean passwordMatches = false;
        try {
            passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        } catch (Exception ignored) {
            // 防止旧明文格式导致 BCrypt 内部抛出异常
        }

        if (!passwordMatches && request.getPassword().equals(user.getPassword())) {
            passwordMatches = true;
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            updateById(user);
            log.info("检测到用户 [{}] 使用明文存储，已自动升级为 BCrypt 哈希加密", user.getUsername());
        }

        if (!passwordMatches) {
            int currentFailCount = (user.getLoginFailCount() != null ? user.getLoginFailCount() : 0) + 1;
            user.setLoginFailCount(currentFailCount);

            if (currentFailCount >= maxLoginAttempts) {
                user.setLockTime(now.plusMinutes(lockDurationMinutes));
                updateById(user);
                log.warn("用户登录失败达到上限被锁定: username={}, failCount={}", user.getUsername(), currentFailCount);
                throw BusinessException.of(ResultCode.TOO_MANY_REQUESTS,
                        String.format("密码连续错误已达 %d 次，账号已被锁定 %d 分钟", maxLoginAttempts, lockDurationMinutes));
            } else {
                updateById(user);
                int remainingAttempts = maxLoginAttempts - currentFailCount;
                throw BusinessException.of(ResultCode.UNAUTHORIZED,
                        String.format("用户名或密码错误，剩余 %d 次尝试机会", remainingAttempts));
            }
        }

        // 5. 登录成功，重置失败计数与锁定期
        if ((user.getLoginFailCount() != null && user.getLoginFailCount() > 0) || user.getLockTime() != null) {
            user.setLoginFailCount(0);
            user.setLockTime(null);
            updateById(user);
        }

        // 6. 生成双 Token
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        log.info("用户登录成功: id={}, username={}, role={}", user.getId(), user.getUsername(), user.getRole());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtUtil.getAccessTokenExpiry())
                .user(toUserInfoVO(user))
                .build();
    }

    @Override
    public Map<String, Object> refreshToken(RefreshTokenRequest request) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(request.getRefreshToken());
            String tokenType = jwtUtil.getTypeFromToken(request.getRefreshToken());

            if (!"refresh".equals(tokenType)) {
                throw BusinessException.unauthorized("无效的 Refresh Token");
            }

            User user = getById(userId);
            if (user == null || (user.getStatus() != null && user.getStatus() == 0)) {
                throw BusinessException.unauthorized("用户不存在或已被禁用");
            }

            // 重新颁发双 Token
            String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole());
            String newRefreshToken = jwtUtil.generateRefreshToken(user.getId());

            Map<String, Object> result = new HashMap<>();
            result.put("accessToken", newAccessToken);
            result.put("refreshToken", newRefreshToken);
            result.put("expiresIn", jwtUtil.getAccessTokenExpiry());
            return result;
        } catch (Exception e) {
            log.warn("Refresh Token 验证失败: {}", e.getMessage());
            throw BusinessException.unauthorized("Refresh Token 已失效，请重新登录");
        }
    }

    @Override
    public UserInfoVO getCurrentUserInfo(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }
        return toUserInfoVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoVO updateProfile(Long userId, UpdateProfileRequest request) {
        User user = getById(userId);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }

        if (StringUtils.hasText(request.getNickname())) {
            user.setNickname(request.getNickname());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (StringUtils.hasText(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getContactInfo() != null) {
            user.setContactInfo(request.getContactInfo());
        }

        updateById(user);
        return toUserInfoVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = getById(userId);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }

        // 校验原密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw BusinessException.badRequest("原密码输入错误");
        }

        // 校验新旧密码不能相同
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw BusinessException.badRequest("新密码不能与原密码相同");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        updateById(user);
        log.info("用户修改密码成功: userId={}", userId);
    }

    @Override
    public UserInfoVO toUserInfoVO(User user) {
        if (user == null) {
            return null;
        }
        return UserInfoVO.builder()
                .id(user.getId())
                .studentId(user.getStudentId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .phone(user.getPhone())
                .email(user.getEmail())
                .contactInfo(user.getContactInfo())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
