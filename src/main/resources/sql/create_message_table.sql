-- 创建消息表
CREATE TABLE IF NOT EXISTS `messages` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` bigint(20) NOT NULL COMMENT '发送者ID',
  `sender_name` varchar(100) NOT NULL COMMENT '发送者姓名',
  `sender_type` varchar(20) NOT NULL DEFAULT 'teacher' COMMENT '发送者类型(teacher/admin/system)',
  `receiver_id` bigint(20) NOT NULL COMMENT '接收者ID',
  `application_id` bigint(20) DEFAULT NULL COMMENT '关联申请ID',
  `title` varchar(200) NOT NULL COMMENT '消息标题',
  `content` text NOT NULL COMMENT '消息内容',
  `message_type` varchar(50) NOT NULL DEFAULT 'notification' COMMENT '消息类型(notification/reminder/warning/info)',
  `priority` varchar(20) NOT NULL DEFAULT 'normal' COMMENT '优先级(high/normal/low)',
  `is_read` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已读(0-未读,1-已读)',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `requires_reply` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否需要回复(0-不需要,1-需要)',
  `reply_deadline` datetime DEFAULT NULL COMMENT '回复截止时间',
  `attachment_url` varchar(500) DEFAULT NULL COMMENT '附件URL',
  `expires_at` datetime DEFAULT NULL COMMENT '消息过期时间',
  `status` varchar(20) NOT NULL DEFAULT 'active' COMMENT '消息状态(active/archived/deleted)',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_receiver_id` (`receiver_id`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_application_id` (`application_id`),
  KEY `idx_message_type` (`message_type`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- 插入一些示例数据
INSERT INTO `messages` (`sender_id`, `sender_name`, `sender_type`, `receiver_id`, `application_id`, `title`, `content`, `message_type`, `priority`, `is_read`) VALUES
(1, '张教授', 'teacher', 1, 1, '申请材料审核通知', '您的申请材料已通过初步审核，请准备面试相关材料。', 'notification', 'high', 0),
(1, '张教授', 'teacher', 1, 1, '面试时间安排', '您的面试时间安排在下周三上午10:00，请提前准备。', 'reminder', 'high', 0),
(2, '李老师', 'teacher', 1, 2, '推荐信提醒', '请尽快提交推荐信，截止时间为本月底。', 'reminder', 'normal', 1),
(1, '张教授', 'teacher', 1, 1, '申请进度更新', '您的申请已进入最终审核阶段，预计一周内出结果。', 'info', 'normal', 0),
(3, '王导师', 'teacher', 1, 3, '奖学金申请通知', '恭喜您获得奖学金申请资格，请查看详细要求。', 'notification', 'high', 0);