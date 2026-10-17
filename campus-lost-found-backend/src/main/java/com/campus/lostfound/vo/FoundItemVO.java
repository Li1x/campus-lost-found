package com.campus.lostfound.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 招领信息返回 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoundItemVO {

    private Long id;

    private Long userId;

    private String userNickname;

    private String userAvatar;

    private Integer categoryId;

    private String categoryName;

    private String title;

    private String description;

    private String foundLocation;

    private LocalDate foundTime;

    private String storageLocation;

    /** 联系方式（权限校验脱敏） */
    private String contactInfo;

    private String status;

    private Long reviewerId;

    private String reviewComment;

    private LocalDateTime reviewedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /** 图片 URL 列表 */
    private List<String> images;
}
