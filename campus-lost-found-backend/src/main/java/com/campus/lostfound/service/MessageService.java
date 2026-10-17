package com.campus.lostfound.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.lostfound.entity.Message;
import com.campus.lostfound.vo.MessageVO;

/**
 * 站内消息服务接口
 */
public interface MessageService extends IService<Message> {

    /**
     * 发送系统站内通知
     */
    void sendNotification(Long userId, String title, String content, String type, Long relatedId, String relatedType);

    /**
     * 分页查询当前用户的消息列表
     */
    Page<MessageVO> pageUserMessages(Long userId, int page, int size);

    /**
     * 将单条消息标记为已读
     */
    void markRead(Long userId, Long messageId);

    /**
     * 将用户全部未读消息标记为已读
     */
    void markAllRead(Long userId);

    /**
     * 查询用户未读消息数量
     */
    long countUnreadMessages(Long userId);
}
