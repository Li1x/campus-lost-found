package com.campus.lostfound.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 修改用户账号状态请求 DTO
 */
@Data
public class UserStatusRequest {

    /** 账号状态：0-禁用 1-启用 */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
