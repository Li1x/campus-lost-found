package com.campus.lostfound.common.enums;

import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter
public enum UserRole {

    STUDENT("STUDENT", "学生"),
    ADMIN("ADMIN", "管理员"),
    SUPER_ADMIN("SUPER_ADMIN", "超级管理员");

    private final String code;
    private final String label;

    UserRole(String code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * 判断是否具有管理权限（管理员或超级管理员）
     */
    public boolean isAdmin() {
        return this == ADMIN || this == SUPER_ADMIN;
    }

    /**
     * 根据 code 获取枚举
     */
    public static UserRole fromCode(String code) {
        for (UserRole role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("未知角色: " + code);
    }
}
