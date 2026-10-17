package com.campus.lostfound.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.lostfound.common.enums.ClaimStatus;
import com.campus.lostfound.common.enums.ItemStatus;
import com.campus.lostfound.common.enums.ItemType;
import com.campus.lostfound.common.exception.BusinessException;
import com.campus.lostfound.dto.ClaimCreateRequest;
import com.campus.lostfound.dto.OwnerAuditRequest;
import com.campus.lostfound.entity.Claim;
import com.campus.lostfound.entity.FoundItem;
import com.campus.lostfound.entity.LostItem;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.mapper.ClaimMapper;
import com.campus.lostfound.mapper.FoundItemMapper;
import com.campus.lostfound.mapper.LostItemMapper;
import com.campus.lostfound.mapper.UserMapper;
import com.campus.lostfound.service.ClaimService;
import com.campus.lostfound.service.MessageService;
import com.campus.lostfound.vo.ClaimVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 认领申请服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimServiceImpl extends ServiceImpl<ClaimMapper, Claim> implements ClaimService {

    private final LostItemMapper lostItemMapper;
    private final FoundItemMapper foundItemMapper;
    private final UserMapper userMapper;
    private final MessageService messageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClaimVO createClaim(Long claimantId, ClaimCreateRequest request) {
        Long ownerId;
        String itemTitle;

        // 1. 验证目标物品状态并获取发布者
        if (ItemType.LOST.getCode().equalsIgnoreCase(request.getItemType())) {
            LostItem item = lostItemMapper.selectById(request.getItemId());
            if (item == null) {
                throw BusinessException.notFound("寻物信息不存在");
            }
            if (!ItemStatus.APPROVED.getCode().equals(item.getStatus())) {
                throw BusinessException.badRequest("该物品当前不可申请认领");
            }
            ownerId = item.getUserId();
            itemTitle = item.getTitle();
        } else if (ItemType.FOUND.getCode().equalsIgnoreCase(request.getItemType())) {
            FoundItem item = foundItemMapper.selectById(request.getItemId());
            if (item == null) {
                throw BusinessException.notFound("招领信息不存在");
            }
            if (!ItemStatus.APPROVED.getCode().equals(item.getStatus())) {
                throw BusinessException.badRequest("该物品当前不可申请认领");
            }
            ownerId = item.getUserId();
            itemTitle = item.getTitle();
        } else {
            throw BusinessException.badRequest("未知的物品类型");
        }

        // 2. 防刷：不能认领自己发布的物品
        if (claimantId.equals(ownerId)) {
            throw BusinessException.badRequest("您不能对本人发布的物品发起认领");
        }

        // 3. 防重：不能对同一物品重复发起待处理认领
        long pendingCount = count(new LambdaQueryWrapper<Claim>()
                .eq(Claim::getItemType, request.getItemType().toUpperCase())
                .eq(Claim::getItemId, request.getItemId())
                .eq(Claim::getClaimantId, claimantId)
                .in(Claim::getStatus, ClaimStatus.PENDING.getCode(), ClaimStatus.OWNER_APPROVED.getCode()));
        if (pendingCount > 0) {
            throw BusinessException.conflict("您对此物品已有正在处理中的认领申请，请耐心等待核对");
        }

        // 4. 保存认领申请
        Claim claim = new Claim();
        claim.setItemType(request.getItemType().toUpperCase());
        claim.setItemId(request.getItemId());
        claim.setClaimantId(claimantId);
        claim.setDescription(request.getDescription());
        claim.setContactInfo(request.getContactInfo());
        claim.setStatus(ClaimStatus.PENDING.getCode());
        save(claim);

        // 5. 触发站内通知给发布者
        User claimant = userMapper.selectById(claimantId);
        String claimantName = claimant != null ? claimant.getNickname() : "有同学";
        messageService.sendNotification(
                ownerId,
                "收到新的物品认领核对申请",
                String.format("同学 [%s] 对您发布的物品【%s】发起了认领核对，请前往“我的认领”页面进行信息核实。", claimantName, itemTitle),
                "CLAIM_NOTIFY",
                claim.getId(),
                "CLAIM"
        );

        log.info("用户提交认领申请: claimId={}, claimantId={}, itemId={}", claim.getId(), claimantId, request.getItemId());
        return convertToVO(claim);
    }

    @Override
    public Page<ClaimVO> pageMyClaims(Long claimantId, int page, int size) {
        Page<Claim> claimPage = page(new Page<>(page, size), new LambdaQueryWrapper<Claim>()
                .eq(Claim::getClaimantId, claimantId)
                .orderByDesc(Claim::getCreatedAt));

        Page<ClaimVO> voPage = new Page<>(claimPage.getCurrent(), claimPage.getSize(), claimPage.getTotal());
        List<ClaimVO> voList = claimPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public Page<ClaimVO> pageReceivedClaims(Long ownerId, int page, int size) {
        // 获取当前用户发布的全部失物和招领 ID 列表
        List<Long> lostIds = lostItemMapper.selectList(new LambdaQueryWrapper<LostItem>()
                .select(LostItem::getId)
                .eq(LostItem::getUserId, ownerId)).stream().map(LostItem::getId).collect(Collectors.toList());

        List<Long> foundIds = foundItemMapper.selectList(new LambdaQueryWrapper<FoundItem>()
                .select(FoundItem::getId)
                .eq(FoundItem::getUserId, ownerId)).stream().map(FoundItem::getId).collect(Collectors.toList());

        if (lostIds.isEmpty() && foundIds.isEmpty()) {
            return new Page<>(page, size, 0);
        }

        LambdaQueryWrapper<Claim> wrapper = new LambdaQueryWrapper<Claim>()
                .and(w -> {
                    if (!lostIds.isEmpty()) {
                        w.or(sub -> sub.eq(Claim::getItemType, ItemType.LOST.getCode()).in(Claim::getItemId, lostIds));
                    }
                    if (!foundIds.isEmpty()) {
                        w.or(sub -> sub.eq(Claim::getItemType, ItemType.FOUND.getCode()).in(Claim::getItemId, foundIds));
                    }
                })
                .orderByDesc(Claim::getCreatedAt);

        Page<Claim> claimPage = page(new Page<>(page, size), wrapper);
        Page<ClaimVO> voPage = new Page<>(claimPage.getCurrent(), claimPage.getSize(), claimPage.getTotal());
        List<ClaimVO> voList = claimPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void ownerAudit(Long claimId, Long ownerId, OwnerAuditRequest request) {
        Claim claim = getById(claimId);
        if (claim == null) {
            throw BusinessException.notFound("认领申请不存在");
        }

        if (!ClaimStatus.PENDING.getCode().equals(claim.getStatus())) {
            throw BusinessException.badRequest("该认领申请已处于非待确认状态，不可重复操作");
        }

        // 校验操作人是否为该物品发布者
        Long realOwnerId = getOwnerIdOfItem(claim.getItemType(), claim.getItemId());
        if (!ownerId.equals(realOwnerId)) {
            throw BusinessException.forbidden("您无权核验非本人发布的物品认领申请");
        }

        String itemTitle = getItemTitle(claim.getItemType(), claim.getItemId());

        if (Boolean.TRUE.equals(request.getApproved())) {
            claim.setStatus(ClaimStatus.OWNER_APPROVED.getCode());
            claim.setOwnerComment(request.getComment() != null ? request.getComment() : "发布者已核验无误");
            claim.setOwnerReviewedAt(LocalDateTime.now());
            updateById(claim);

            // 发送通知给申请人
            messageService.sendNotification(
                    claim.getClaimantId(),
                    "认领申请已通过发布者核实",
                    String.format("您对物品【%s】的认领申请已得到发布者确认，目前已转入管理员终审环节，请留意进一步通知。", itemTitle),
                    "CLAIM_NOTIFY",
                    claim.getId(),
                    "CLAIM"
            );
        } else {
            claim.setStatus(ClaimStatus.OWNER_REJECTED.getCode());
            claim.setOwnerComment(request.getComment() != null ? request.getComment() : "信息不匹配");
            claim.setOwnerReviewedAt(LocalDateTime.now());
            updateById(claim);

            // 发送通知给申请人
            messageService.sendNotification(
                    claim.getClaimantId(),
                    "认领申请未通过发布者核实",
                    String.format("您对物品【%s】的认领申请已被发布者驳回。原因：%s", itemTitle, claim.getOwnerComment()),
                    "CLAIM_RESULT",
                    claim.getId(),
                    "CLAIM"
            );
        }

        log.info("发布者核验认领申请: claimId={}, approved={}, ownerId={}", claimId, request.getApproved(), ownerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelClaim(Long claimId, Long claimantId) {
        Claim claim = getById(claimId);
        if (claim == null) {
            throw BusinessException.notFound("认领申请不存在");
        }

        if (!claim.getClaimantId().equals(claimantId)) {
            throw BusinessException.forbidden("您无权撤销他人的认领申请");
        }

        if (!ClaimStatus.PENDING.getCode().equals(claim.getStatus())) {
            throw BusinessException.badRequest("只能撤销处于待核实状态的认领申请");
        }

        claim.setStatus(ClaimStatus.CANCELLED.getCode());
        updateById(claim);
        log.info("申请人撤销认领: claimId={}, claimantId={}", claimId, claimantId);
    }

    private Long getOwnerIdOfItem(String itemType, Long itemId) {
        if (ItemType.LOST.getCode().equalsIgnoreCase(itemType)) {
            LostItem item = lostItemMapper.selectById(itemId);
            return item != null ? item.getUserId() : null;
        } else {
            FoundItem item = foundItemMapper.selectById(itemId);
            return item != null ? item.getUserId() : null;
        }
    }

    private String getItemTitle(String itemType, Long itemId) {
        if (ItemType.LOST.getCode().equalsIgnoreCase(itemType)) {
            LostItem item = lostItemMapper.selectById(itemId);
            return item != null ? item.getTitle() : "失物";
        } else {
            FoundItem item = foundItemMapper.selectById(itemId);
            return item != null ? item.getTitle() : "招领";
        }
    }

    private ClaimVO convertToVO(Claim claim) {
        String itemTitle = getItemTitle(claim.getItemType(), claim.getItemId());

        String claimantNickname = null;
        String claimantPhone = null;
        User claimant = userMapper.selectById(claim.getClaimantId());
        if (claimant != null) {
            claimantNickname = claimant.getNickname();
            claimantPhone = claimant.getPhone();
        }

        return ClaimVO.builder()
                .id(claim.getId())
                .itemType(claim.getItemType())
                .itemId(claim.getItemId())
                .itemTitle(itemTitle)
                .claimantId(claim.getClaimantId())
                .claimantNickname(claimantNickname)
                .claimantPhone(claimantPhone)
                .description(claim.getDescription())
                .contactInfo(claim.getContactInfo())
                .status(claim.getStatus())
                .ownerReviewedAt(claim.getOwnerReviewedAt())
                .ownerComment(claim.getOwnerComment())
                .reviewerId(claim.getReviewerId())
                .reviewComment(claim.getReviewComment())
                .reviewedAt(claim.getReviewedAt())
                .createdAt(claim.getCreatedAt())
                .build();
    }
}
