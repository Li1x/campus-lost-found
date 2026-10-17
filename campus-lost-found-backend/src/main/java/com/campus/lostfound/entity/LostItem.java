package com.campus.lostfound.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 失物信息实体
 */
@Data
@TableName("lf_lost_item")
public class LostItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 发布者 ID */
    private Long userId;

    /** 分类 ID */
    private Integer categoryId;

    /** 物品名称 */
    private String title;

    /** 详细描述 */
    private String description;

    /** 丢失地点 */
    private String lostLocation;

    /** 丢失日期 */
    private LocalDate lostTime;

    /** 联系方式 */
    private String contactInfo;

    /** 状态：PENDING/APPROVED/REJECTED/CLAIMED/COMPLETED/CLOSED/EXPIRED */
    private String status;

    /** 审核人 ID */
    private Long reviewerId;

    /** 审核意见 */
    private String reviewComment;

    /** 审核时间 */
    private LocalDateTime reviewedAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 逻辑删除 */
    @TableLogic
    private Integer deleted;
}
