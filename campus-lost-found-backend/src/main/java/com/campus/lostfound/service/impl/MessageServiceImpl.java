package com.campus.lostfound.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.lostfound.entity.Message;
import com.campus.lostfound.mapper.MessageMapper;
import com.campus.lostfound.service.MessageService;
import com.campus.lostfound.vo.MessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 站内消息服务实现
 */
@Slf4j
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendNotification(Long userId, String title, String content, String type, Long relatedId, String relatedType) {
        Message message = new Message();
        message.setUserId(userId);
        message.setTitle(title);
        message.setContent(content);
        message.setType(type);
        message.setRelatedId(relatedId);
        message.setRelatedType(relatedType);
        message.setIsRead(0);
        save(message);
        log.info("发送站内通知: userId={}, title={}", userId, title);
    }

    @Override
    public Page<MessageVO> pageUserMessages(Long userId, int page, int size) {
        Page<Message> msgPage = page(new Page<>(page, size), new LambdaQueryWrapper<Message>()
                .eq(Message::getUserId, userId)
                .orderByDesc(Message::getCreatedAt));

        Page<MessageVO> voPage = new Page<>(msgPage.getCurrent(), msgPage.getSize(), msgPage.getTotal());
        List<MessageVO> voList = msgPage.getRecords().stream().map(m -> MessageVO.builder()
                .id(m.getId())
                .userId(m.getUserId())
                .title(m.getTitle())
                .content(m.getContent())
                .type(m.getType())
                .relatedId(m.getRelatedId())
                .relatedType(m.getRelatedType())
                .isRead(m.getIsRead())
                .createdAt(m.getCreatedAt())
                .build()
        ).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markRead(Long userId, Long messageId) {
        lambdaUpdate()
                .eq(Message::getId, messageId)
                .eq(Message::getUserId, userId)
                .set(Message::getIsRead, 1)
                .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllRead(Long userId) {
        lambdaUpdate()
                .eq(Message::getUserId, userId)
                .eq(Message::getIsRead, 0)
                .set(Message::getIsRead, 1)
                .update();
    }

    @Override
    public long countUnreadMessages(Long userId) {
        return count(new LambdaQueryWrapper<Message>()
                .eq(Message::getUserId, userId)
                .eq(Message::getIsRead, 0));
    }
}
