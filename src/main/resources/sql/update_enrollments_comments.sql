-- 更新 enrollments 表字段注释
-- 说明：application_id 字段实际存储的是 application_schools 表的 id，而不是 applications 表的 id

-- 更新 application_id 字段注释
ALTER TABLE `enrollments` 
MODIFY COLUMN `application_id` bigint(20) NOT NULL COMMENT '申请学校ID (对应 application_schools 表的 id)';

-- 如果需要添加外键约束，可以执行以下语句（根据实际情况决定是否启用）
-- ALTER TABLE `enrollments` 
-- ADD CONSTRAINT `fk_enrollment_application_school` 
-- FOREIGN KEY (`application_id`) REFERENCES `application_schools` (`id`) ON DELETE CASCADE;