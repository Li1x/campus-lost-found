package com.campus.lostfound.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册请求 DTO
 */
@Data
public class RegisterRequest {

    /**
     * 学号（数字与字母，长度不超过32位）
     */
    @NotBlank(message = "学号不能为空")
    @Size(min = 4, max = 32, message = "学号长度必须在4-32位之间")
    private String studentId;

    /**
     * 用户名（登录名，字母开头数字下划线）
     */
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", message = "用户名必须为3-20位的字母、数字或下划线")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度必须在6-32位之间")
    private String password;

    /**
     * 真实姓名或昵称
     */
    @NotBlank(message = "昵称不能为空")
    @Size(max = 20, message = "昵称长度不能超过20位")
    private String nickname;

    /**
     * 手机号码
     */
    @NotBlank(message = "手机号码不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;

    /**
     * 电子邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * QQ/微信等其他联系方式
     */
    private String contactInfo;
}
