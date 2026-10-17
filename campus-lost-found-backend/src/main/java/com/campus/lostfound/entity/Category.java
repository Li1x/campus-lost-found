package com.campus.lostfound.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 物品分类实体
 */
@Data
@TableName("sys_category")
public class Category {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 分类名称 */
    private String name;

    /** 图标标识 */
    private String icon;

    /** 排序权重 */
    private Integer sortOrder;

    /** 状态：0-禁用 1-启用 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
