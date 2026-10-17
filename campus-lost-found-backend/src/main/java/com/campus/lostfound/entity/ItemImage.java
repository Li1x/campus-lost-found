package com.campus.lostfound.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 物品图片实体
 */
@Data
@TableName("lf_item_image")
public class ItemImage {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 类型：LOST / FOUND */
    private String itemType;

    /** 物品 ID */
    private Long itemId;

    /** 图片存储路径 */
    private String url;

    /** 排序 */
    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
