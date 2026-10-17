package com.campus.lostfound.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.lostfound.common.Result;
import com.campus.lostfound.security.UserContext;
import com.campus.lostfound.service.MessageService;
import com.campus.lostfound.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 站内消息控制器
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * 分页查询当前用户消息
     */
    @GetMapping
    public Result<Page<MessageVO>> listMessages(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Long userId = UserContext.getCurrentUserId();
        Page<MessageVO> pageResult = messageService.pageUserMessages(userId, page, size);
        return Result.ok(pageResult);
    }

    /**
     * 获取未读消息计数
     */
    @GetMapping("/unread-count")
    public Result<Map<String, Long>> getUnreadCount() {
        Long userId = UserContext.getCurrentUserId();
        long count = messageService.countUnreadMessages(userId);
        return Result.ok(Map.of("unreadCount", count));
    }

    /**
     * 标记单条消息为已读
     */
    @PutMapping("/{id}/read")
    public Result<Void> markRead(@PathVariable("id") Long id) {
        Long userId = UserContext.getCurrentUserId();
        messageService.markRead(userId, id);
        return Result.ok("已标记为已读");
    }

    /**
     * 全部标记为已读
     */
    @PutMapping("/read-all")
    public Result<Void> markAllRead() {
        Long userId = UserContext.getCurrentUserId();
        messageService.markAllRead(userId);
        return Result.ok("已全部标记为已读");
    }
}
