package com.campus.lostfound.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 认领申请实体
 */
@Data
@TableName("lf_claim")
public class Claim {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 类型：LOST / FOUND */
    private String itemType;

    /** 物品 ID */
    private Long itemId;

    /** 认领人 ID */
    private Long claimantId;

    /** 认领说明 */
    private String description;

    /** 联系方式 */
    private String contactInfo;

    /** 认领状态：PENDING/OWNER_APPROVED/OWNER_REJECTED/APPROVED/REJECTED/CANCELLED */
    private String status;

    /** 发布者确认时间 */
    private LocalDateTime ownerReviewedAt;

    /** 发布者确认意见 */
    private String ownerComment;

    /** 管理员审核人 ID */
    private Long reviewerId;

    /** 管理员审核意见 */
    private String reviewComment;

    /** 管理员审核时间 */
    private LocalDateTime reviewedAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
