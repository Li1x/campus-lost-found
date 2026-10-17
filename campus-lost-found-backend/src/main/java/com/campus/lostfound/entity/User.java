package com.campus.lostfound.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("sys_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 学号 */
    private String studentId;

    /** 用户名 */
    private String username;

    /** BCrypt 加密密码 */
    private String password;

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

    /** 登录失败次数 */
    private Integer loginFailCount;

    /** 锁定到期时间 */
    private LocalDateTime lockTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 逻辑删除 */
    @TableLogic
    private Integer deleted;
}
