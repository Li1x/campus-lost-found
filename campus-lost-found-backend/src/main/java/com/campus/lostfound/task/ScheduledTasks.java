package com.campus.lostfound.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.lostfound.common.enums.ItemStatus;
import com.campus.lostfound.entity.FoundItem;
import com.campus.lostfound.entity.LostItem;
import com.campus.lostfound.mapper.FoundItemMapper;
import com.campus.lostfound.mapper.LostItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 物品自动过期与日常运维定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final LostItemMapper lostItemMapper;
    private final FoundItemMapper foundItemMapper;

    @Value("${business.item.auto-expire-days:30}")
    private int autoExpireDays;

    /**
     * 每日凌晨 2 点执行自动过期检查
     * 将创建时间超过 30 天且仍处于 APPROVED 状态的物品标记为 EXPIRED
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void scanAndExpireItems() {
        LocalDateTime thresholdTime = LocalDateTime.now().minusDays(autoExpireDays);
        log.info("开始执行物品自动过期扫描任务，过期阈值时间: {}", thresholdTime);

        // 1. 处理失物信息
        List<LostItem> expiredLostList = lostItemMapper.selectList(new LambdaQueryWrapper<LostItem>()
                .eq(LostItem::getStatus, ItemStatus.APPROVED.getCode())
                .le(LostItem::getCreatedAt, thresholdTime));

        for (LostItem item : expiredLostList) {
            item.setStatus(ItemStatus.EXPIRED.getCode());
            lostItemMapper.updateById(item);
        }

        // 2. 处理招领信息
        List<FoundItem> expiredFoundList = foundItemMapper.selectList(new LambdaQueryWrapper<FoundItem>()
                .eq(FoundItem::getStatus, ItemStatus.APPROVED.getCode())
                .le(FoundItem::getCreatedAt, thresholdTime));

        for (FoundItem item : expiredFoundList) {
            item.setStatus(ItemStatus.EXPIRED.getCode());
            foundItemMapper.updateById(item);
        }

        log.info("物品自动过期扫描完成：失物过期 {} 条，招领过期 {} 条", expiredLostList.size(), expiredFoundList.size());
    }
}
