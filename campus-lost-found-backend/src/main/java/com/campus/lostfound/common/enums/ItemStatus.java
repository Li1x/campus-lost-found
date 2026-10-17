package com.campus.lostfound.common.enums;

import lombok.Getter;

/**
 * 物品状态枚举
 * 适用于失物（LostItem）和招领（FoundItem）
 */
@Getter
public enum ItemStatus {

    PENDING("PENDING", "待审核"),
    APPROVED("APPROVED", "已通过"),
    REJECTED("REJECTED", "已拒绝"),
    CLAIMED("CLAIMED", "已认领"),
    COMPLETED("COMPLETED", "已完成"),
    CLOSED("CLOSED", "已关闭"),
    EXPIRED("EXPIRED", "已过期");

    private final String code;
    private final String label;

    ItemStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public static ItemStatus fromCode(String code) {
        for (ItemStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知物品状态: " + code);
    }
}
