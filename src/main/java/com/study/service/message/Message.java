package com.study.service.message;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息实体类
 * 用于存储老师发送给学生的消息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 发送者ID (老师/管理员ID)
     */
    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    /**
     * 发送者姓名
     */
    @Column(name = "sender_name", length = 100)
    private String senderName;

    /**
     * 发送者类型 (teacher, admin, system)
     */
    @Column(name = "sender_type", length = 20, nullable = false)
    private String senderType;

    /**
     * 接收者ID (学生ID)
     */
    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    /**
     * 关联申请ID (可选，如果消息与特定申请相关)
     */
    @Column(name = "application_id")
    private Long applicationId;

    /**
     * 消息标题
     */
    @Column(length = 200)
    private String title;

    /**
     * 消息内容
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /**
     * 消息类型 (notification, reminder, update, alert)
     */
    @Column(name = "message_type", length = 20)
    private String messageType;

    /**
     * 优先级 (low, normal, high, urgent)
     */
    @Column(length = 10)
    private String priority;

    /**
     * 是否已读
     */
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    /**
     * 阅读时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "read_at")
    private Date readAt;

    /**
     * 是否需要回复
     */
    @Column(name = "requires_reply")
    private Boolean requiresReply = false;

    /**
     * 是否已回复
     */
    @Column(name = "is_replied")
    private Boolean isReplied = false;

    /**
     * 回复时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "replied_at")
    private Date repliedAt;

    /**
     * 附件URL (可选)
     */
    @Column(name = "attachment_url", length = 500)
    private String attachmentUrl;

    /**
     * 过期时间 (可选)
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expires_at")
    private Date expiresAt;

    /**
     * 消息状态 (active, archived, deleted)
     */
    @Column(length = 20)
    private String status = "active";

    /**
     * 创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    /**
     * 更新时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;
}