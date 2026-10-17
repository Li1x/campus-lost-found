package com.campus.lostfound.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 站内通知返回 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageVO {

    private Long id;

    private Long userId;

    private String title;

    private String content;

    private String type;

    private Long relatedId;

    private String relatedType;

    private Integer isRead;

    private LocalDateTime createdAt;
}
