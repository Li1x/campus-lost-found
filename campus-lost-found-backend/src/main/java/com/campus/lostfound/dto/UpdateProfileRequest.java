package com.campus.lostfound.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改个人信息请求 DTO
 */
@Data
public class UpdateProfileRequest {

    /**
     * 昵称
     */
    @Size(max = 20, message = "昵称长度不能超过20位")
    private String nickname;

    /**
     * 头像 URL
     */
    private String avatar;

    /**
     * 手机号码
     */
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
