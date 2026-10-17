package com.campus.lostfound.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 创建招领启事请求 DTO
 */
@Data
public class FoundItemCreateRequest {

    /** 物品分类 ID */
    @NotNull(message = "分类不能为空")
    private Integer categoryId;

    /** 物品名称 */
    @NotBlank(message = "物品名称不能为空")
    @Size(max = 100, message = "物品名称不能超过100字")
    private String title;

    /** 详细描述 */
    @NotBlank(message = "详细描述不能为空")
    @Size(max = 1000, message = "详细描述不能超过1000字")
    private String description;

    /** 拾得地点 */
    @NotBlank(message = "拾得地点不能为空")
    @Size(max = 100, message = "拾得地点不能超过100字")
    private String foundLocation;

    /** 拾得日期 */
    @NotNull(message = "拾得日期不能为空")
    private LocalDate foundTime;

    /** 现存放位置 */
    @Size(max = 100, message = "存放位置不能超过100字")
    private String storageLocation;

    /** 联系方式 */
    @NotBlank(message = "联系方式不能为空")
    @Size(max = 100, message = "联系方式不能超过100字")
    private String contactInfo;

    /** 物品图片 URL 列表 */
    private List<String> images;
}
