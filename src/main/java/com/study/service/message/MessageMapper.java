package com.study.service.message;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 消息数据访问层接口
 */
@Mapper
public interface MessageMapper {

    /**
     * 根据接收者ID获取消息列表
     * @param receiverId 接收者ID
     * @return 消息列表
     */
    List<Message> findByReceiverId(@Param("receiverId") Long receiverId);

    /**
     * 根据接收者ID和申请ID获取消息列表
     * @param receiverId 接收者ID
     * @param applicationId 申请ID
     * @return 消息列表
     */
    List<Message> findByReceiverIdAndApplicationId(@Param("receiverId") Long receiverId, 
                                                   @Param("applicationId") Long applicationId);

    /**
     * 根据ID获取消息详情
     * @param id 消息ID
     * @return 消息详情
     */
    Message findById(@Param("id") Long id);

    /**
     * 插入新消息
     * @param message 消息对象
     * @return 影响行数
     */
    int insert(Message message);

    /**
     * 更新消息
     * @param message 消息对象
     * @return 影响行数
     */
    int update(Message message);

    /**
     * 标记消息为已读
     * @param id 消息ID
     * @return 影响行数
     */
    int markAsRead(@Param("id") Long id);

    /**
     * 批量标记消息为已读
     * @param ids 消息ID列表
     * @return 影响行数
     */
    int markAsReadBatch(@Param("ids") List<Long> ids);

    /**
     * 删除消息
     * @param id 消息ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 获取未读消息数量
     * @param receiverId 接收者ID
     * @return 未读消息数量
     */
    int countUnreadMessages(@Param("receiverId") Long receiverId);

    /**
     * 根据发送者ID获取消息列表
     * @param senderId 发送者ID
     * @return 消息列表
     */
    List<Message> findBySenderId(@Param("senderId") Long senderId);

    /**
     * 根据消息类型获取消息列表
     * @param receiverId 接收者ID
     * @param messageType 消息类型
     * @return 消息列表
     */
    List<Message> findByReceiverIdAndType(@Param("receiverId") Long receiverId, 
                                          @Param("messageType") String messageType);
}