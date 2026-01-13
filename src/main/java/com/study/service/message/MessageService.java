package com.study.service.message;

import java.util.List;

/**
 * 消息服务接口
 */
public interface MessageService {

    /**
     * 根据接收者ID获取消息列表
     * @param receiverId 接收者ID
     * @return 消息列表
     */
    List<Message> getMessagesByReceiverId(Long receiverId);

    /**
     * 根据接收者ID和申请ID获取消息列表
     * @param receiverId 接收者ID
     * @param applicationId 申请ID
     * @return 消息列表
     */
    List<Message> getMessagesByReceiverIdAndApplicationId(Long receiverId, Long applicationId);

    /**
     * 根据ID获取消息详情
     * @param id 消息ID
     * @return 消息详情
     */
    Message getMessageById(Long id);

    /**
     * 发送消息
     * @param message 消息对象
     * @return 是否发送成功
     */
    boolean sendMessage(Message message);

    /**
     * 更新消息
     * @param message 消息对象
     * @return 是否更新成功
     */
    boolean updateMessage(Message message);

    /**
     * 标记消息为已读
     * @param id 消息ID
     * @return 是否标记成功
     */
    boolean markMessageAsRead(Long id);

    /**
     * 批量标记消息为已读
     * @param ids 消息ID列表
     * @return 是否标记成功
     */
    boolean markMessagesAsRead(List<Long> ids);

    /**
     * 删除消息
     * @param id 消息ID
     * @return 是否删除成功
     */
    boolean deleteMessage(Long id);

    /**
     * 获取未读消息数量
     * @param receiverId 接收者ID
     * @return 未读消息数量
     */
    int getUnreadMessageCount(Long receiverId);

    /**
     * 根据发送者ID获取消息列表
     * @param senderId 发送者ID
     * @return 消息列表
     */
    List<Message> getMessagesBySenderId(Long senderId);

    /**
     * 根据消息类型获取消息列表
     * @param receiverId 接收者ID
     * @param messageType 消息类型
     * @return 消息列表
     */
    List<Message> getMessagesByType(Long receiverId, String messageType);

    /**
     * 发送系统通知
     * @param receiverId 接收者ID
     * @param title 标题
     * @param content 内容
     * @param applicationId 关联申请ID（可选）
     * @return 是否发送成功
     */
    boolean sendSystemNotification(Long receiverId, String title, String content, Long applicationId);

    /**
     * 发送老师消息
     * @param senderId 发送者ID
     * @param senderName 发送者姓名
     * @param receiverId 接收者ID
     * @param title 标题
     * @param content 内容
     * @param applicationId 关联申请ID（可选）
     * @return 是否发送成功
     */
    boolean sendTeacherMessage(Long senderId, String senderName, Long receiverId, 
                              String title, String content, Long applicationId);
}