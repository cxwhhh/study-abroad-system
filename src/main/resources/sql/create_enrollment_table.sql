-- 创建入学信息表
CREATE TABLE IF NOT EXISTS `enrollments` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '入学记录ID',
  `application_id` bigint(20) NOT NULL COMMENT '申请学校ID (对应 application_schools 表的 id)',
  `student_id` bigint(20) NOT NULL COMMENT '学生ID',
  `school_id` bigint(20) NOT NULL COMMENT '学校ID',
  `program_id` bigint(20) DEFAULT NULL COMMENT '专业ID',
  `enrollment_date` datetime NOT NULL COMMENT '入学时间',
  `semester` varchar(20) DEFAULT NULL COMMENT '学期(Spring/Fall/Summer)',
  `academic_year` int(4) DEFAULT NULL COMMENT '学年',
  `tuition_fee` decimal(10,2) DEFAULT NULL COMMENT '学费金额',
  `commission_amount` decimal(10,2) DEFAULT NULL COMMENT '返点金额',
  `commission_status` varchar(20) NOT NULL DEFAULT 'pending' COMMENT '返点状态(pending/paid/cancelled)',
  `commission_paid_date` datetime DEFAULT NULL COMMENT '返点支付时间',
  `notes` text DEFAULT NULL COMMENT '备注信息',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建者ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_application_id` (`application_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_school_id` (`school_id`),
  KEY `idx_program_id` (`program_id`),
  KEY `idx_enrollment_date` (`enrollment_date`),
  KEY `idx_academic_year` (`academic_year`),
  KEY `idx_commission_status` (`commission_status`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='入学信息表';

-- 添加外键约束（如果需要的话，可以根据实际情况调整）
-- ALTER TABLE `enrollments` ADD CONSTRAINT `fk_enrollment_application_school` FOREIGN KEY (`application_id`) REFERENCES `application_schools` (`id`) ON DELETE CASCADE;
-- ALTER TABLE `enrollments` ADD CONSTRAINT `fk_enrollment_student` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
-- ALTER TABLE `enrollments` ADD CONSTRAINT `fk_enrollment_school` FOREIGN KEY (`school_id`) REFERENCES `schools` (`id`) ON DELETE CASCADE;