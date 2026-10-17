package com.campus.lostfound.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.lostfound.common.enums.ClaimStatus;
import com.campus.lostfound.common.enums.ItemStatus;
import com.campus.lostfound.common.enums.ItemType;
import com.campus.lostfound.common.enums.UserRole;
import com.campus.lostfound.common.exception.BusinessException;
import com.campus.lostfound.dto.AdminAuditRequest;
import com.campus.lostfound.dto.UserStatusRequest;
import com.campus.lostfound.entity.*;
import com.campus.lostfound.mapper.*;
import com.campus.lostfound.service.AdminService;
import com.campus.lostfound.service.MessageService;
import com.campus.lostfound.vo.ClaimVO;
import com.campus.lostfound.vo.DashboardStatsVO;
import com.campus.lostfound.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理员服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final LostItemMapper lostItemMapper;
    private final FoundItemMapper foundItemMapper;
    private final ClaimMapper claimMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final MessageService messageService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<Map<String, Object>> pagePendingItems(int page, int size) {
        // 分别查询待审核的失物与招领
        List<LostItem> lostItems = lostItemMapper.selectList(new LambdaQueryWrapper<LostItem>()
                .eq(LostItem::getStatus, ItemStatus.PENDING.getCode())
                .orderByDesc(LostItem::getCreatedAt));

        List<FoundItem> foundItems = foundItemMapper.selectList(new LambdaQueryWrapper<FoundItem>()
                .eq(FoundItem::getStatus, ItemStatus.PENDING.getCode())
                .orderByDesc(FoundItem::getCreatedAt));

        List<Map<String, Object>> combined = new ArrayList<>();
        for (LostItem item : lostItems) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("itemType", ItemType.LOST.getCode());
            map.put("title", item.getTitle());
            map.put("description", item.getDescription());
            map.put("location", item.getLostLocation());
            map.put("itemDate", item.getLostTime());
            map.put("createdAt", item.getCreatedAt());
            combined.add(map);
        }
        for (FoundItem item : foundItems) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("itemType", ItemType.FOUND.getCode());
            map.put("title", item.getTitle());
            map.put("description", item.getDescription());
            map.put("location", item.getFoundLocation());
            map.put("itemDate", item.getFoundTime());
            map.put("createdAt", item.getCreatedAt());
            combined.add(map);
        }

        // 统一按创建时间降序
        combined.sort((a, b) -> ((LocalDateTime) b.get("createdAt")).compareTo((LocalDateTime) a.get("createdAt")));

        int total = combined.size();
        int fromIndex = Math.min((page - 1) * size, total);
        int toIndex = Math.min(fromIndex + size, total);

        Page<Map<String, Object>> pageResult = new Page<>(page, size, total);
        pageResult.setRecords(combined.subList(fromIndex, toIndex));
        return pageResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditItem(String itemType, Long id, Long reviewerId, AdminAuditRequest request) {
        boolean approved = Boolean.TRUE.equals(request.getApproved());
        String status = approved ? ItemStatus.APPROVED.getCode() : ItemStatus.REJECTED.getCode();
        String comment = request.getComment() != null ? request.getComment() : (approved ? "审核通过" : "内容不合规驳回");

        Long publisherId;
        String title;

        if (ItemType.LOST.getCode().equalsIgnoreCase(itemType)) {
            LostItem item = lostItemMapper.selectById(id);
            if (item == null) throw BusinessException.notFound("寻物信息不存在");
            item.setStatus(status);
            item.setReviewerId(reviewerId);
            item.setReviewComment(comment);
            item.setReviewedAt(LocalDateTime.now());
            lostItemMapper.updateById(item);
            publisherId = item.getUserId();
            title = item.getTitle();
        } else if (ItemType.FOUND.getCode().equalsIgnoreCase(itemType)) {
            FoundItem item = foundItemMapper.selectById(id);
            if (item == null) throw BusinessException.notFound("招领信息不存在");
            item.setStatus(status);
            item.setReviewerId(reviewerId);
            item.setReviewComment(comment);
            item.setReviewedAt(LocalDateTime.now());
            foundItemMapper.updateById(item);
            publisherId = item.getUserId();
            title = item.getTitle();
        } else {
            throw BusinessException.badRequest("未知的物品类型");
        }

        // 发送审核结果通知给发布者
        String msgTitle = approved ? "您发布的物品已审核通过" : "您发布的物品未通过审核";
        String msgContent = approved
                ? String.format("您发布的【%s】已通过管理员审核，现已面向全校展示。", title)
                : String.format("您发布的【%s】未通过审核。原因：%s", title, comment);

        messageService.sendNotification(publisherId, msgTitle, msgContent, "REVIEW_RESULT", id, itemType.toUpperCase());
        log.info("管理员审核物品: type={}, id={}, status={}, reviewerId={}", itemType, id, status, reviewerId);
    }

    @Override
    public Page<ClaimVO> pagePendingClaims(int page, int size) {
        Page<Claim> claimPage = claimMapper.selectPage(new Page<>(page, size), new LambdaQueryWrapper<Claim>()
                .eq(Claim::getStatus, ClaimStatus.OWNER_APPROVED.getCode())
                .orderByDesc(Claim::getOwnerReviewedAt));

        Page<ClaimVO> voPage = new Page<>(claimPage.getCurrent(), claimPage.getSize(), claimPage.getTotal());
        List<ClaimVO> voList = claimPage.getRecords().stream().map(c -> {
            String title = "未知物品";
            if (ItemType.LOST.getCode().equalsIgnoreCase(c.getItemType())) {
                LostItem item = lostItemMapper.selectById(c.getItemId());
                if (item != null) title = item.getTitle();
            } else {
                FoundItem item = foundItemMapper.selectById(c.getItemId());
                if (item != null) title = item.getTitle();
            }

            User claimant = userMapper.selectById(c.getClaimantId());
            String nickname = claimant != null ? claimant.getNickname() : null;
            String phone = claimant != null ? claimant.getPhone() : null;

            return ClaimVO.builder()
                    .id(c.getId())
                    .itemType(c.getItemType())
                    .itemId(c.getItemId())
                    .itemTitle(title)
                    .claimantId(c.getClaimantId())
                    .claimantNickname(nickname)
                    .claimantPhone(phone)
                    .description(c.getDescription())
                    .contactInfo(c.getContactInfo())
                    .status(c.getStatus())
                    .ownerReviewedAt(c.getOwnerReviewedAt())
                    .ownerComment(c.getOwnerComment())
                    .reviewerId(c.getReviewerId())
                    .reviewComment(c.getReviewComment())
                    .reviewedAt(c.getReviewedAt())
                    .createdAt(c.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditClaim(Long claimId, Long reviewerId, AdminAuditRequest request) {
        Claim claim = claimMapper.selectById(claimId);
        if (claim == null) {
            throw BusinessException.notFound("认领记录不存在");
        }

        if (!ClaimStatus.OWNER_APPROVED.getCode().equals(claim.getStatus())) {
            throw BusinessException.badRequest("该认领尚未通过发布者初审或已被处理");
        }

        boolean approved = Boolean.TRUE.equals(request.getApproved());
        String comment = request.getComment() != null ? request.getComment() : (approved ? "管理员终审通过" : "凭证不足驳回");

        claim.setStatus(approved ? ClaimStatus.APPROVED.getCode() : ClaimStatus.REJECTED.getCode());
        claim.setReviewerId(reviewerId);
        claim.setReviewComment(comment);
        claim.setReviewedAt(LocalDateTime.now());
        claimMapper.updateById(claim);

        String itemTitle = "物品";
        Long ownerId = null;

        if (ItemType.LOST.getCode().equalsIgnoreCase(claim.getItemType())) {
            LostItem item = lostItemMapper.selectById(claim.getItemId());
            if (item != null) {
                itemTitle = item.getTitle();
                ownerId = item.getUserId();
                if (approved) {
                    item.setStatus(ItemStatus.CLAIMED.getCode());
                    lostItemMapper.updateById(item);
                }
            }
        } else {
            FoundItem item = foundItemMapper.selectById(claim.getItemId());
            if (item != null) {
                itemTitle = item.getTitle();
                ownerId = item.getUserId();
                if (approved) {
                    item.setStatus(ItemStatus.CLAIMED.getCode());
                    foundItemMapper.updateById(item);
                }
            }
        }

        // 通知申请人
        if (approved) {
            messageService.sendNotification(
                    claim.getClaimantId(),
                    "认领终审通过通知",
                    String.format("恭喜！您对物品【%s】的认领申请已终审通过。请联系发布者或前往指定交接地领取您的物品。", itemTitle),
                    "CLAIM_RESULT",
                    claim.getId(),
                    "CLAIM"
            );
            if (ownerId != null) {
                messageService.sendNotification(
                        ownerId,
                        "物品认领流程已完结",
                        String.format("您发布的物品【%s】的认领申请已由管理员终审确认，记录状态已更新为“已认领”。感谢您的爱心与配合！", itemTitle),
                        "CLAIM_RESULT",
                        claim.getId(),
                        "CLAIM"
                    );
            }

            // 关闭该物品其他待处理的认领申请
            List<Claim> otherClaims = claimMapper.selectList(new LambdaQueryWrapper<Claim>()
                    .eq(Claim::getItemType, claim.getItemType())
                    .eq(Claim::getItemId, claim.getItemId())
                    .ne(Claim::getId, claim.getId())
                    .in(Claim::getStatus, ClaimStatus.PENDING.getCode(), ClaimStatus.OWNER_APPROVED.getCode()));

            for (Claim other : otherClaims) {
                other.setStatus(ClaimStatus.REJECTED.getCode());
                other.setReviewComment("该物品已被其他同学成功认领结案");
                other.setReviewedAt(LocalDateTime.now());
                claimMapper.updateById(other);

                messageService.sendNotification(
                        other.getClaimantId(),
                        "认领申请关闭通知",
                        String.format("您对物品【%s】提交的认领申请已关闭，该物品已被先行确认认领归还。", itemTitle),
                        "CLAIM_RESULT",
                        other.getId(),
                        "CLAIM"
                );
            }
        } else {
            messageService.sendNotification(
                    claim.getClaimantId(),
                    "认领终审未通过通知",
                    String.format("很遗憾，您对物品【%s】的认领申请终审未通过。管理员意见：%s", itemTitle, comment),
                    "CLAIM_RESULT",
                    claim.getId(),
                    "CLAIM"
            );
        }

        log.info("管理员终审认领: claimId={}, approved={}, reviewerId={}", claimId, approved, reviewerId);
    }

    @Override
    public Page<UserInfoVO> pageUsers(int page, int size) {
        Page<User> userPage = userMapper.selectPage(new Page<>(page, size), new LambdaQueryWrapper<User>()
                .orderByDesc(User::getCreatedAt));

        Page<UserInfoVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserInfoVO> voList = userPage.getRecords().stream().map(u -> UserInfoVO.builder()
                .id(u.getId())
                .studentId(u.getStudentId())
                .username(u.getUsername())
                .nickname(u.getNickname())
                .avatar(u.getAvatar())
                .phone(u.getPhone())
                .email(u.getEmail())
                .contactInfo(u.getContactInfo())
                .role(u.getRole())
                .status(u.getStatus())
                .createdAt(u.getCreatedAt())
                .build()
        ).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long targetUserId, Long operatorId, UserStatusRequest request) {
        User target = userMapper.selectById(targetUserId);
        if (target == null) throw BusinessException.notFound("目标用户不存在");

        if (UserRole.SUPER_ADMIN.getCode().equals(target.getRole())) {
            throw BusinessException.forbidden("禁止修改超级管理员账号状态");
        }

        target.setStatus(request.getStatus());
        userMapper.updateById(target);
        log.info("管理员变更用户状态: targetUserId={}, status={}, operatorId={}", targetUserId, request.getStatus(), operatorId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetUserPassword(Long targetUserId, Long operatorId) {
        User target = userMapper.selectById(targetUserId);
        if (target == null) throw BusinessException.notFound("目标用户不存在");

        if (UserRole.SUPER_ADMIN.getCode().equals(target.getRole())) {
            throw BusinessException.forbidden("禁止重置超级管理员密码");
        }

        // 默认重置为 123456
        target.setPassword(passwordEncoder.encode("123456"));
        target.setLoginFailCount(0);
        target.setLockTime(null);
        userMapper.updateById(target);
        log.info("管理员重置用户密码为默认密码: targetUserId={}, operatorId={}", targetUserId, operatorId);
    }

    @Override
    public DashboardStatsVO getDashboardStatistics() {
        Long totalLost = lostItemMapper.selectCount(null);
        Long totalFound = foundItemMapper.selectCount(null);
        Long totalClaimSuccess = claimMapper.selectCount(new LambdaQueryWrapper<Claim>()
                .eq(Claim::getStatus, ClaimStatus.APPROVED.getCode()));

        // 今日新增统计
        LocalDate today = LocalDate.now();
        Long lostToday = lostItemMapper.selectCount(new LambdaQueryWrapper<LostItem>()
                .ge(LostItem::getCreatedAt, today.atStartOfDay()));
        Long foundToday = foundItemMapper.selectCount(new LambdaQueryWrapper<FoundItem>()
                .ge(FoundItem::getCreatedAt, today.atStartOfDay()));
        Long todayNew = lostToday + foundToday;

        // 待审事项总数（待审失物 + 待审招领 + 待终审认领）
        Long pendingLost = lostItemMapper.selectCount(new LambdaQueryWrapper<LostItem>()
                .eq(LostItem::getStatus, ItemStatus.PENDING.getCode()));
        Long pendingFound = foundItemMapper.selectCount(new LambdaQueryWrapper<FoundItem>()
                .eq(FoundItem::getStatus, ItemStatus.PENDING.getCode()));
        Long pendingClaim = claimMapper.selectCount(new LambdaQueryWrapper<Claim>()
                .eq(Claim::getStatus, ClaimStatus.OWNER_APPROVED.getCode()));
        Long pendingTotal = pendingLost + pendingFound + pendingClaim;

        // 分类统计
        List<Category> categories = categoryMapper.selectList(new LambdaQueryWrapper<Category>().eq(Category::getStatus, 1));
        List<Map<String, Object>> catDistribution = new ArrayList<>();
        for (Category cat : categories) {
            Long cLost = lostItemMapper.selectCount(new LambdaQueryWrapper<LostItem>().eq(LostItem::getCategoryId, cat.getId()));
            Long cFound = foundItemMapper.selectCount(new LambdaQueryWrapper<FoundItem>().eq(FoundItem::getCategoryId, cat.getId()));
            Map<String, Object> map = new HashMap<>();
            map.put("name", cat.getName());
            map.put("value", cLost + cFound);
            catDistribution.add(map);
        }

        return DashboardStatsVO.builder()
                .totalLostCount(totalLost)
                .totalFoundCount(totalFound)
                .totalClaimSuccessCount(totalClaimSuccess)
                .todayNewCount(todayNew)
                .pendingReviewCount(pendingTotal)
                .categoryDistribution(catDistribution)
                .build();
    }
}
