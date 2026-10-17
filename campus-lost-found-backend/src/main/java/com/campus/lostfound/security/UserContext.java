package com.campus.lostfound.security;

import com.campus.lostfound.common.enums.UserRole;
import lombok.Data;

/**
 * 当前登录用户上下文
 * 通过 ThreadLocal 在请求生命周期内传递用户信息
 */
public class UserContext {

    private static final ThreadLocal<UserInfo> CURRENT_USER = new ThreadLocal<>();

    /**
     * 设置当前用户信息
     */
    public static void setCurrentUser(Long userId, String role) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setRole(UserRole.fromCode(role));
        CURRENT_USER.set(userInfo);
    }

    /**
     * 获取当前用户信息
     */
    public static UserInfo getCurrentUser() {
        return CURRENT_USER.get();
    }

    /**
     * 获取当前用户 ID
     */
    public static Long getCurrentUserId() {
        UserInfo info = CURRENT_USER.get();
        return info != null ? info.getUserId() : null;
    }

    /**
     * 获取当前用户角色
     */
    public static UserRole getCurrentRole() {
        UserInfo info = CURRENT_USER.get();
        return info != null ? info.getRole() : null;
    }

    /**
     * 判断当前用户是否具有管理权限
     */
    public static boolean isAdmin() {
        UserRole role = getCurrentRole();
        return role != null && role.isAdmin();
    }

    /**
     * 清除当前用户信息（请求结束时调用）
     */
    public static void clear() {
        CURRENT_USER.remove();
    }

    /**
     * 用户信息内部类
     */
    @Data
    public static class UserInfo {
        private Long userId;
        private UserRole role;
    }
}
