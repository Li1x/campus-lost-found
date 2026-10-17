package com.campus.lostfound.security;

import com.campus.lostfound.common.enums.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色权限注解
 * 标注在 Controller 方法上，指定允许访问的角色
 * 在 JwtInterceptor 中校验
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {

    /**
     * 允许的角色列表（满足其一即可）
     */
    UserRole[] value();
}
