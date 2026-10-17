-- ============================================
-- 校园失物招领系统 数据库初始化脚本
-- 版本：V1
-- 日期：2026-10-16
-- 数据库：campus_lost_found
-- 字符集：utf8mb4
-- ============================================

CREATE DATABASE IF NOT EXISTS `campus_lost_found`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE `campus_lost_found`;

-- -------------------------------------------
-- 1. 用户表
-- -------------------------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `student_id`       VARCHAR(32)  NOT NULL COMMENT '学号',
    `username`         VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`         VARCHAR(128) NOT NULL COMMENT 'BCrypt加密密码',
    `nickname`         VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    `avatar`           VARCHAR(256) DEFAULT NULL COMMENT '头像URL',
    `phone`            VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `email`            VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `contact_info`     VARCHAR(100) DEFAULT NULL COMMENT 'QQ/微信等联系方式',
    `role`             VARCHAR(20)  NOT NULL DEFAULT 'STUDENT' COMMENT '角色：STUDENT/ADMIN/SUPER_ADMIN',
    `status`           TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
    `login_fail_count` INT          NOT NULL DEFAULT 0 COMMENT '登录失败次数',
    `lock_time`        DATETIME     DEFAULT NULL COMMENT '锁定到期时间',
    `created_at`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`          TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_id` (`student_id`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- -------------------------------------------
-- 2. 物品分类表
-- -------------------------------------------
DROP TABLE IF EXISTS `sys_category`;
CREATE TABLE `sys_category` (
    `id`          INT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        VARCHAR(50)  NOT NULL COMMENT '分类名称',
    `icon`        VARCHAR(50)  DEFAULT NULL COMMENT '图标标识',
    `sort_order`  INT          NOT NULL DEFAULT 0 COMMENT '排序权重',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='物品分类表';

-- -------------------------------------------
-- 3. 失物信息表
-- -------------------------------------------
DROP TABLE IF EXISTS `lf_lost_item`;
CREATE TABLE `lf_lost_item` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`        BIGINT       NOT NULL COMMENT '发布者ID',
    `category_id`    INT          NOT NULL COMMENT '分类ID',
    `title`          VARCHAR(100) NOT NULL COMMENT '物品名称',
    `description`    TEXT         DEFAULT NULL COMMENT '详细描述',
    `lost_location`  VARCHAR(200) NOT NULL COMMENT '丢失地点',
    `lost_time`      DATE         NOT NULL COMMENT '丢失日期',
    `contact_info`   VARCHAR(100) DEFAULT NULL COMMENT '联系方式',
    `status`         VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/APPROVED/REJECTED/CLAIMED/COMPLETED/CLOSED/EXPIRED',
    `reviewer_id`    BIGINT       DEFAULT NULL COMMENT '审核人ID',
    `review_comment` VARCHAR(500) DEFAULT NULL COMMENT '审核意见',
    `reviewed_at`    DATETIME     DEFAULT NULL COMMENT '审核时间',
    `created_at`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_status_created` (`status`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='失物信息表';

-- -------------------------------------------
-- 4. 招领信息表
-- -------------------------------------------
DROP TABLE IF EXISTS `lf_found_item`;
CREATE TABLE `lf_found_item` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`          BIGINT       NOT NULL COMMENT '发布者ID',
    `category_id`      INT          NOT NULL COMMENT '分类ID',
    `title`            VARCHAR(100) NOT NULL COMMENT '物品名称',
    `description`      TEXT         DEFAULT NULL COMMENT '详细描述',
    `found_location`   VARCHAR(200) NOT NULL COMMENT '拾得地点',
    `found_time`       DATE         NOT NULL COMMENT '拾得日期',
    `storage_location` VARCHAR(200) DEFAULT NULL COMMENT '当前存放位置',
    `contact_info`     VARCHAR(100) DEFAULT NULL COMMENT '联系方式',
    `status`           VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/APPROVED/REJECTED/CLAIMED/COMPLETED/CLOSED/EXPIRED',
    `reviewer_id`      BIGINT       DEFAULT NULL COMMENT '审核人ID',
    `review_comment`   VARCHAR(500) DEFAULT NULL COMMENT '审核意见',
    `reviewed_at`      DATETIME     DEFAULT NULL COMMENT '审核时间',
    `created_at`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`          TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_status_created` (`status`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='招领信息表';

-- -------------------------------------------
-- 5. 物品图片表
-- -------------------------------------------
DROP TABLE IF EXISTS `lf_item_image`;
CREATE TABLE `lf_item_image` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `item_type`   VARCHAR(10)  NOT NULL COMMENT '类型：LOST/FOUND',
    `item_id`     BIGINT       NOT NULL COMMENT '物品ID',
    `url`         VARCHAR(512) NOT NULL COMMENT '图片存储路径',
    `sort_order`  INT          NOT NULL DEFAULT 0 COMMENT '排序',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_item` (`item_type`, `item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='物品图片表';

-- -------------------------------------------
-- 6. 认领申请表
-- -------------------------------------------
DROP TABLE IF EXISTS `lf_claim`;
CREATE TABLE `lf_claim` (
    `id`                BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `item_type`         VARCHAR(10)  NOT NULL COMMENT '类型：LOST/FOUND',
    `item_id`           BIGINT       NOT NULL COMMENT '物品ID',
    `claimant_id`       BIGINT       NOT NULL COMMENT '认领人ID',
    `description`       TEXT         NOT NULL COMMENT '认领说明',
    `contact_info`      VARCHAR(100) DEFAULT NULL COMMENT '联系方式',
    `status`            VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT '认领状态：PENDING/OWNER_APPROVED/OWNER_REJECTED/APPROVED/REJECTED/CANCELLED',
    `owner_reviewed_at` DATETIME     DEFAULT NULL COMMENT '发布者确认时间',
    `owner_comment`     VARCHAR(500) DEFAULT NULL COMMENT '发布者确认意见',
    `reviewer_id`       BIGINT       DEFAULT NULL COMMENT '管理员审核人ID',
    `review_comment`    VARCHAR(500) DEFAULT NULL COMMENT '管理员审核意见',
    `reviewed_at`       DATETIME     DEFAULT NULL COMMENT '管理员审核时间',
    `created_at`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_item` (`item_type`, `item_id`),
    KEY `idx_claimant_id` (`claimant_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='认领申请表';

-- -------------------------------------------
-- 7. 站内消息表
-- -------------------------------------------
DROP TABLE IF EXISTS `sys_message`;
CREATE TABLE `sys_message` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`       BIGINT       NOT NULL COMMENT '接收者ID',
    `title`         VARCHAR(200) NOT NULL COMMENT '消息标题',
    `content`       TEXT         NOT NULL COMMENT '消息内容',
    `type`          VARCHAR(20)  NOT NULL COMMENT '类型：REVIEW_RESULT/CLAIM_NOTIFY/CLAIM_RESULT/SYSTEM',
    `related_id`    BIGINT       DEFAULT NULL COMMENT '关联对象ID',
    `related_type`  VARCHAR(20)  DEFAULT NULL COMMENT '关联类型：LOST_ITEM/FOUND_ITEM/CLAIM',
    `is_read`       TINYINT      NOT NULL DEFAULT 0 COMMENT '已读状态：0-未读 1-已读',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_read` (`user_id`, `is_read`),
    KEY `idx_user_created` (`user_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='站内消息表';

-- -------------------------------------------
-- 8. 操作日志表
-- -------------------------------------------
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `operator_id`     BIGINT       NOT NULL COMMENT '操作人ID',
    `operation_type`  VARCHAR(50)  NOT NULL COMMENT '操作类型',
    `target_type`     VARCHAR(50)  NOT NULL COMMENT '目标类型：LOST_ITEM/FOUND_ITEM/CLAIM/USER',
    `target_id`       BIGINT       NOT NULL COMMENT '目标ID',
    `detail`          TEXT         DEFAULT NULL COMMENT '操作详情（JSON格式）',
    `ip_address`      VARCHAR(50)  DEFAULT NULL COMMENT '操作IP',
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_operator` (`operator_id`),
    KEY `idx_target` (`target_type`, `target_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作日志表';

-- ============================================
-- 初始数据
-- ============================================

-- 超级管理员（默认密码：admin123）
-- BCrypt 哈希值通过 Spring Security BCryptPasswordEncoder 生成
INSERT INTO `sys_user` (`student_id`, `username`, `password`, `nickname`, `role`, `status`)
VALUES ('ADMIN001', 'admin', '$2a$10$EqKZhOmGBiRMJWbSBfBJPeVGOLiBMYTBdKnLcMS1C5KDfH4bEQb0G', '超级管理员', 'SUPER_ADMIN', 1);

-- 预置物品分类
INSERT INTO `sys_category` (`name`, `icon`, `sort_order`, `status`) VALUES
('证件',     'id-card',    1, 1),
('电子设备', 'phone',      2, 1),
('书籍文具', 'book',       3, 1),
('衣物饰品', 'shirt',      4, 1),
('钥匙卡片', 'key',        5, 1),
('运动器材', 'football',   6, 1),
('生活用品', 'toiletries', 7, 1),
('其他',     'box',        8, 1);
