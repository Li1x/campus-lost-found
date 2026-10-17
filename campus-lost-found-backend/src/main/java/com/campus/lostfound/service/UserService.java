package com.campus.lostfound.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.lostfound.dto.ChangePasswordRequest;
import com.campus.lostfound.dto.LoginRequest;
import com.campus.lostfound.dto.RefreshTokenRequest;
import com.campus.lostfound.dto.RegisterRequest;
import com.campus.lostfound.dto.UpdateProfileRequest;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.vo.LoginResponse;
import com.campus.lostfound.vo.UserInfoVO;

import java.util.Map;

/**
 * 用户业务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 注册成功的用户信息
     */
    UserInfoVO register(RegisterRequest request);

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 包含 Token 和用户信息的响应
     */
    LoginResponse login(LoginRequest request);

    /**
     * 刷新 Token
     *
     * @param request 刷新请求
     * @return 包含新 Token 的响应
     */
    Map<String, Object> refreshToken(RefreshTokenRequest request);

    /**
     * 获取当前登录用户信息
     *
     * @param userId 当前用户ID
     * @return 用户信息VO
     */
    UserInfoVO getCurrentUserInfo(Long userId);

    /**
     * 更新当前用户个人信息
     *
     * @param userId  当前用户ID
     * @param request 个人信息
     * @return 更新后的用户信息VO
     */
    UserInfoVO updateProfile(Long userId, UpdateProfileRequest request);

    /**
     * 修改密码
     *
     * @param userId  当前用户ID
     * @param request 密码请求
     */
    void changePassword(Long userId, ChangePasswordRequest request);

    /**
     * 将 User 实体转换为安全脱敏的 UserInfoVO
     *
     * @param user 用户实体
     * @return UserInfoVO
     */
    UserInfoVO toUserInfoVO(User user);
}
