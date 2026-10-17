package com.campus.lostfound.common.enums;

import lombok.Getter;

/**
 * 认领状态枚举
 */
@Getter
public enum ClaimStatus {

    PENDING("PENDING", "待确认"),
    OWNER_APPROVED("OWNER_APPROVED", "发布者已确认"),
    OWNER_REJECTED("OWNER_REJECTED", "发布者已拒绝"),
    APPROVED("APPROVED", "已通过"),
    REJECTED("REJECTED", "已拒绝"),
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String label;

    ClaimStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public static ClaimStatus fromCode(String code) {
        for (ClaimStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知认领状态: " + code);
    }
}
