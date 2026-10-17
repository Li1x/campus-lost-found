package com.campus.lostfound.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 发布者初审认领申请请求 DTO
 */
@Data
public class OwnerAuditRequest {

    /** 是否确认匹配（true-同意，false-驳回） */
    @NotNull(message = "审核结果不能为空")
    private Boolean approved;

    /** 审核意见 */
    @Size(max = 255, message = "审核意见不能超过255字")
    private String comment;
}
