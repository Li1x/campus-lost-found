package com.campus.lostfound.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 站内消息实体
 */
@Data
@TableName("sys_message")
public class Message {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 接收者 ID */
    private Long userId;

    /** 消息标题 */
    private String title;

    /** 消息内容 */
    private String content;

    /** 类型：REVIEW_RESULT / CLAIM_NOTIFY / CLAIM_RESULT / SYSTEM */
    private String type;

    /** 关联对象 ID */
    private Long relatedId;

    /** 关联类型：LOST_ITEM / FOUND_ITEM / CLAIM */
    private String relatedType;

    /** 已读状态：0-未读 1-已读 */
    private Integer isRead;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
