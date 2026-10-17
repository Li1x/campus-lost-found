package com.campus.lostfound.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 发起认领申请请求 DTO
 */
@Data
public class ClaimCreateRequest {

    /** 物品类型：LOST / FOUND */
    @NotBlank(message = "物品类型不能为空")
    private String itemType;

    /** 物品 ID */
    @NotNull(message = "物品ID不能为空")
    private Long itemId;

    /** 认领凭证与说明 */
    @NotBlank(message = "认领凭证与说明不能为空")
    @Size(max = 1000, message = "认领说明不能超过1000字")
    private String description;

    /** 认领人联系方式 */
    @NotBlank(message = "联系方式不能为空")
    @Size(max = 100, message = "联系方式不能超过100字")
    private String contactInfo;
}
