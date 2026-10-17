package com.campus.lostfound.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体
 */
@Data
@TableName("sys_operation_log")
public class OperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 操作人 ID */
    private Long operatorId;

    /** 操作类型 */
    private String operationType;

    /** 目标类型：LOST_ITEM / FOUND_ITEM / CLAIM / USER */
    private String targetType;

    /** 目标 ID */
    private Long targetId;

    /** 操作详情（JSON 格式） */
    private String detail;

    /** 操作 IP */
    private String ipAddress;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
