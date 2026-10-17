package com.campus.lostfound.controller;

import com.campus.lostfound.common.Result;
import com.campus.lostfound.dto.ChangePasswordRequest;
import com.campus.lostfound.dto.UpdateProfileRequest;
import com.campus.lostfound.security.UserContext;
import com.campus.lostfound.service.UserService;
import com.campus.lostfound.vo.UserInfoVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 个人中心与用户信息控制器
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取当前登录用户的详细个人信息
     */
    @GetMapping("/info")
    public Result<UserInfoVO> getCurrentUserInfo() {
        Long userId = UserContext.getCurrentUserId();
        UserInfoVO userInfo = userService.getCurrentUserInfo(userId);
        return Result.ok(userInfo);
    }

    /**
     * 更新当前用户个人信息
     */
    @PutMapping("/profile")
    public Result<UserInfoVO> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        Long userId = UserContext.getCurrentUserId();
        UserInfoVO userInfo = userService.updateProfile(userId, request);
        return Result.ok("个人信息更新成功", userInfo);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = UserContext.getCurrentUserId();
        userService.changePassword(userId, request);
        return Result.ok("密码修改成功，请重新登录");
    }

    /**
     * 获取指定用户的公开基础信息（非本人时脱敏敏感联系方式）
     */
    @GetMapping("/{id}")
    public Result<UserInfoVO> getUserPublicInfo(@PathVariable("id") Long targetUserId) {
        UserInfoVO userInfo = userService.getCurrentUserInfo(targetUserId);
        Long currentUserId = UserContext.getCurrentUserId();

        // 若不是本人且不是管理员，对敏感信息进行脱敏或隐藏
        if (!targetUserId.equals(currentUserId) && !UserContext.isAdmin()) {
            userInfo.setStudentId(null);
            userInfo.setEmail(null);
            if (userInfo.getPhone() != null && userInfo.getPhone().length() == 11) {
                // 手机号脱敏：138****1234
                userInfo.setPhone(userInfo.getPhone().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
            }
        }

        return Result.ok(userInfo);
    }
}
