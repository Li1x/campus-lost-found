package com.campus.lostfound.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.lostfound.common.enums.ItemStatus;
import com.campus.lostfound.common.enums.ItemType;
import com.campus.lostfound.common.exception.BusinessException;
import com.campus.lostfound.dto.FoundItemCreateRequest;
import com.campus.lostfound.entity.Category;
import com.campus.lostfound.entity.FoundItem;
import com.campus.lostfound.entity.ItemImage;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.mapper.CategoryMapper;
import com.campus.lostfound.mapper.FoundItemMapper;
import com.campus.lostfound.mapper.ItemImageMapper;
import com.campus.lostfound.mapper.UserMapper;
import com.campus.lostfound.security.UserContext;
import com.campus.lostfound.service.FoundItemService;
import com.campus.lostfound.vo.FoundItemVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 招领信息服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FoundItemServiceImpl extends ServiceImpl<FoundItemMapper, FoundItem> implements FoundItemService {

    private final ItemImageMapper itemImageMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FoundItemVO createFoundItem(Long userId, FoundItemCreateRequest request) {
        // 1. 验证分类
        Category category = categoryMapper.selectById(request.getCategoryId());
        if (category == null || category.getStatus() == 0) {
            throw BusinessException.badRequest("所选分类不存在或已禁用");
        }

        // 2. 创建实体（默认待审核 PENDING）
        FoundItem item = new FoundItem();
        item.setUserId(userId);
        item.setCategoryId(request.getCategoryId());
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setFoundLocation(request.getFoundLocation());
        item.setFoundTime(request.getFoundTime());
        item.setStorageLocation(request.getStorageLocation());
        item.setContactInfo(request.getContactInfo());
        item.setStatus(ItemStatus.PENDING.getCode());

        save(item);

        // 3. 关联保存图片
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            int order = 0;
            for (String imgUrl : request.getImages()) {
                ItemImage image = new ItemImage();
                image.setItemType(ItemType.FOUND.getCode());
                image.setItemId(item.getId());
                image.setUrl(imgUrl);
                image.setSortOrder(order++);
                itemImageMapper.insert(image);
            }
        }

        log.info("用户提交招领信息成功: itemId={}, userId={}", item.getId(), userId);
        return convertToVO(item, userId, false);
    }

    @Override
    public Page<FoundItemVO> pagePublicFoundItems(Integer categoryId, String keyword, int page, int size) {
        LambdaQueryWrapper<FoundItem> wrapper = new LambdaQueryWrapper<FoundItem>()
                .eq(FoundItem::getStatus, ItemStatus.APPROVED.getCode())
                .eq(categoryId != null, FoundItem::getCategoryId, categoryId)
                .and(StringUtils.hasText(keyword), w -> w
                        .like(FoundItem::getTitle, keyword)
                        .or()
                        .like(FoundItem::getDescription, keyword)
                        .or()
                        .like(FoundItem::getFoundLocation, keyword)
                )
                .orderByDesc(FoundItem::getCreatedAt);

        Page<FoundItem> itemPage = page(new Page<>(page, size), wrapper);
        Page<FoundItemVO> voPage = new Page<>(itemPage.getCurrent(), itemPage.getSize(), itemPage.getTotal());

        Long currentUserId = UserContext.getCurrentUserId();
        List<FoundItemVO> voList = itemPage.getRecords().stream()
                .map(item -> convertToVO(item, currentUserId, true))
                .collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public Page<FoundItemVO> pageMyFoundItems(Long userId, int page, int size) {
        LambdaQueryWrapper<FoundItem> wrapper = new LambdaQueryWrapper<FoundItem>()
                .eq(FoundItem::getUserId, userId)
                .orderByDesc(FoundItem::getCreatedAt);

        Page<FoundItem> itemPage = page(new Page<>(page, size), wrapper);
        Page<FoundItemVO> voPage = new Page<>(itemPage.getCurrent(), itemPage.getSize(), itemPage.getTotal());

        List<FoundItemVO> voList = itemPage.getRecords().stream()
                .map(item -> convertToVO(item, userId, false))
                .collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public FoundItemVO getFoundItemDetail(Long id, Long currentUserId) {
        FoundItem item = getById(id);
        if (item == null) {
            throw BusinessException.notFound("招领信息不存在或已删除");
        }

        return convertToVO(item, currentUserId, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeFoundItem(Long id, Long currentUserId) {
        FoundItem item = getById(id);
        if (item == null) {
            throw BusinessException.notFound("招领信息不存在");
        }

        boolean isOwner = item.getUserId().equals(currentUserId);
        boolean isAdmin = UserContext.isAdmin();
        if (!isOwner && !isAdmin) {
            throw BusinessException.forbidden("您无权关闭此招领信息");
        }

        item.setStatus(ItemStatus.CLOSED.getCode());
        updateById(item);
        log.info("关闭招领启事: id={}, operatorId={}", id, currentUserId);
    }

    private FoundItemVO convertToVO(FoundItem item, Long currentUserId, boolean isListMode) {
        String catName = null;
        if (item.getCategoryId() != null) {
            Category cat = categoryMapper.selectById(item.getCategoryId());
            if (cat != null) catName = cat.getName();
        }

        String nickname = null;
        String avatar = null;
        User user = userMapper.selectById(item.getUserId());
        if (user != null) {
            nickname = user.getNickname();
            avatar = user.getAvatar();
        }

        // 图片列表
        List<String> images = itemImageMapper.selectList(new LambdaQueryWrapper<ItemImage>()
                        .eq(ItemImage::getItemType, ItemType.FOUND.getCode())
                        .eq(ItemImage::getItemId, item.getId())
                        .orderByAsc(ItemImage::getSortOrder))
                .stream()
                .map(ItemImage::getUrl)
                .collect(Collectors.toList());

        String contactInfo = item.getContactInfo();
        boolean isOwner = currentUserId != null && currentUserId.equals(item.getUserId());
        boolean isAdmin = UserContext.isAdmin();
        if (!isOwner && !isAdmin && contactInfo != null) {
            if (contactInfo.length() == 11 && contactInfo.startsWith("1")) {
                contactInfo = contactInfo.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            } else if (contactInfo.length() > 4) {
                contactInfo = contactInfo.substring(0, 2) + "****" + contactInfo.substring(contactInfo.length() - 2);
            }
        }

        return FoundItemVO.builder()
                .id(item.getId())
                .userId(item.getUserId())
                .userNickname(nickname)
                .userAvatar(avatar)
                .categoryId(item.getCategoryId())
                .categoryName(catName)
                .title(item.getTitle())
                .description(item.getDescription())
                .foundLocation(item.getFoundLocation())
                .foundTime(item.getFoundTime())
                .storageLocation(item.getStorageLocation())
                .contactInfo(contactInfo)
                .status(item.getStatus())
                .reviewerId(item.getReviewerId())
                .reviewComment(item.getReviewComment())
                .reviewedAt(item.getReviewedAt())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .images(images)
                .build();
    }
}
