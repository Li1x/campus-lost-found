package com.campus.lostfound.common.enums;

import lombok.Getter;

/**
 * 物品类型枚举（失物/招领）
 */
@Getter
public enum ItemType {

    LOST("LOST", "失物"),
    FOUND("FOUND", "招领");

    private final String code;
    private final String label;

    ItemType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public static ItemType fromCode(String code) {
        for (ItemType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知物品类型: " + code);
    }
}
