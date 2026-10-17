package com.campus.lostfound.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户信息视图对象（VO）
 * 排除密码、加盐、删除标记等内部敏感信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVO {

    private Long id;

    /** 学号 */
    private String studentId;

    /** 用户名 */
    private String username;

    /** 昵称 */
    private String nickname;

    /** 头像 URL */
    private String avatar;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** QQ/微信等联系方式 */
    private String contactInfo;

    /** 角色：STUDENT / ADMIN / SUPER_ADMIN */
    private String role;

    /** 状态：0-禁用 1-正常 */
    private Integer status;

    /** 注册时间 */
    private LocalDateTime createdAt;
}
