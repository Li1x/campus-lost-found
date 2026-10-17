package com.campus.lostfound.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 管理员审核请求 DTO（通用于物品审核与认领终审）
 */
@Data
public class AdminAuditRequest {

    /** 是否审核通过（true-通过，false-驳回） */
    @NotNull(message = "审核结果不能为空")
    private Boolean approved;

    /** 审核意见 */
    @Size(max = 255, message = "审核意见不能超过255字")
    private String comment;
}
