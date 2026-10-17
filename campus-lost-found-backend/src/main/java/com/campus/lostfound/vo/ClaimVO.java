package com.campus.lostfound.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 认领申请返回 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimVO {

    private Long id;

    private String itemType;

    private Long itemId;

    private String itemTitle;

    private Long claimantId;

    private String claimantNickname;

    private String claimantPhone;

    private String description;

    private String contactInfo;

    private String status;

    private LocalDateTime ownerReviewedAt;

    private String ownerComment;

    private Long reviewerId;

    private String reviewComment;

    private LocalDateTime reviewedAt;

    private LocalDateTime createdAt;
}
