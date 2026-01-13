package com.study.service.message;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 消息服务实现类
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public List<Message> getMessagesByReceiverId(Long receiverId) {
        return messageMapper.findByReceiverId(receiverId);
    }

    @Override
    public List<Message> getMessagesByReceiverIdAndApplicationId(Long receiverId, Long applicationId) {
        return messageMapper.findByReceiverIdAndApplicationId(receiverId, applicationId);
    }

    @Override
    public Message getMessageById(Long id) {
        return messageMapper.findById(id);
    }

    @Override
    public boolean sendMessage(Message message) {
        if (message.getCreatedAt() == null) {
            message.setCreatedAt(new Date());
        }
        if (message.getUpdatedAt() == null) {
            message.setUpdatedAt(new Date());
        }
        if (message.getStatus() == null) {
            message.setStatus("active");
        }
        if (message.getIsRead() == null) {
            message.setIsRead(false);
        }
        if (message.getRequiresReply() == null) {
            message.setRequiresReply(false);
        }
        if (message.getIsReplied() == null) {
            message.setIsReplied(false);
        }
        
        return messageMapper.insert(message) > 0;
    }

    @Override
    public boolean updateMessage(Message message) {
        message.setUpdatedAt(new Date());
        return messageMapper.update(message) > 0;
    }

    @Override
    public boolean markMessageAsRead(Long id) {
        return messageMapper.markAsRead(id) > 0;
    }

    @Override
    public boolean markMessagesAsRead(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        return messageMapper.markAsReadBatch(ids) > 0;
    }

    @Override
    public boolean deleteMessage(Long id) {
        return messageMapper.deleteById(id) > 0;
    }

    @Override
    public int getUnreadMessageCount(Long receiverId) {
        return messageMapper.countUnreadMessages(receiverId);
    }

    @Override
    public List<Message> getMessagesBySenderId(Long senderId) {
        return messageMapper.findBySenderId(senderId);
    }

    @Override
    public List<Message> getMessagesByType(Long receiverId, String messageType) {
        return messageMapper.findByReceiverIdAndType(receiverId, messageType);
    }

    @Override
    public boolean sendSystemNotification(Long receiverId, String title, String content, Long applicationId) {
        Message message = new Message();
        message.setSenderId(0L); // 系统消息使用0作为发送者ID
        message.setSenderName("系统通知");
        message.setSenderType("system");
        message.setReceiverId(receiverId);
        message.setApplicationId(applicationId);
        message.setTitle(title);
        message.setContent(content);
        message.setMessageType("notification");
        message.setPriority("normal");
        
        return sendMessage(message);
    }

    @Override
    public boolean sendTeacherMessage(Long senderId, String senderName, Long receiverId, 
                                     String title, String content, Long applicationId) {
        Message message = new Message();
        message.setSenderId(senderId);
        message.setSenderName(senderName);
        message.setSenderType("teacher");
        message.setReceiverId(receiverId);
        message.setApplicationId(applicationId);
        message.setTitle(title);
        message.setContent(content);
        message.setMessageType("notification");
        message.setPriority("normal");
        
        return sendMessage(message);
    }
}