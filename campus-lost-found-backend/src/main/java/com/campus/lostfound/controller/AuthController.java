package com.campus.lostfound.controller;

import com.campus.lostfound.common.Result;
import com.campus.lostfound.dto.LoginRequest;
import com.campus.lostfound.dto.RefreshTokenRequest;
import com.campus.lostfound.dto.RegisterRequest;
import com.campus.lostfound.security.UserContext;
import com.campus.lostfound.service.UserService;
import com.campus.lostfound.vo.LoginResponse;
import com.campus.lostfound.vo.UserInfoVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证与授权控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<UserInfoVO> register(@Valid @RequestBody RegisterRequest request) {
        UserInfoVO userInfo = userService.register(request);
        return Result.ok("注册成功", userInfo);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return Result.ok("登录成功", response);
    }

    /**
     * 刷新 Access Token
     */
    @PostMapping("/refresh")
    public Result<Map<String, Object>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        Map<String, Object> tokenMap = userService.refreshToken(request);
        return Result.ok("Token 刷新成功", tokenMap);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        Long userId = UserContext.getCurrentUserId();
        log.info("用户退出登录: userId={}", userId);
        return Result.ok("退出登录成功");
    }
}
