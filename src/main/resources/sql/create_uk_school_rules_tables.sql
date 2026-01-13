CREATE TABLE IF NOT EXISTS `undergrad_schools` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `canonical_name` varchar(255) NOT NULL COMMENT '标准中文名（canonical）',
    `canonical_english_name` varchar(255) DEFAULT NULL COMMENT '标准英文名（canonical，可选）',
    `canonical_key` varchar(255) NOT NULL COMMENT '标准化唯一键（用于去重/导入对齐）',
    `is_985` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否985（用于兜底分组）',
    `is_211` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否211（用于兜底分组）',
    `is_double_first_class` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否双一流（用于兜底分组）',
    `school_type` varchar(40) DEFAULT NULL COMMENT '学校类型（public/private/independent 或 公办/民办/独立学院）',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_undergrad_school_canonical_key` (`canonical_key`),
    KEY `idx_undergrad_school_canonical_name` (`canonical_name`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '中国本科院校标准化（全局）';

CREATE TABLE IF NOT EXISTS `undergrad_school_aliases` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `undergrad_school_id` bigint NOT NULL COMMENT '本科院校标准ID（undergrad_schools.id）',
    `alias` varchar(255) NOT NULL COMMENT '别名原文（输入值）',
    `alias_norm` varchar(255) NOT NULL COMMENT '别名归一化（用于匹配）',
    `lang` varchar(10) DEFAULT NULL COMMENT '语言（zh/en等，可选）',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_undergrad_school_alias_norm` (`alias_norm`),
    KEY `idx_undergrad_school_alias_school_id` (`undergrad_school_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '本科院校别名表（用于归一化）';

CREATE TABLE IF NOT EXISTS `uk_school_undergrad_group` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `target_school_id` bigint NOT NULL COMMENT '英国目标学校ID（schools.id）',
    `undergrad_school_id` bigint NOT NULL COMMENT '中国本科院校标准ID（undergrad_schools.id）',
    `group_code` varchar(30) NOT NULL COMMENT '分组代码（Group1/2/3/4 或 TierA/B/C/D 等）',
    `version` varchar(50) DEFAULT NULL COMMENT '版本（可选）',
    `effective_from` datetime DEFAULT NULL COMMENT '生效开始时间（可选）',
    `effective_to` datetime DEFAULT NULL COMMENT '生效结束时间（可选）',
    `priority` int DEFAULT 0 COMMENT '优先级（数值越大越优先）',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_target_undergrad_unique` (
        `target_school_id`,
        `undergrad_school_id`,
        `group_code`,
        `version`
    ),
    KEY `idx_target_school_id` (`target_school_id`),
    KEY `idx_undergrad_school_id` (`undergrad_school_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '按英国目标学校维度的本科院校分组映射';

CREATE TABLE IF NOT EXISTS `uk_school_group_fallback` (
    `target_school_id` bigint NOT NULL COMMENT '英国目标学校ID（schools.id）',
    `fallback_strategy` varchar(30) NOT NULL DEFAULT 'DEFAULT_GROUP' COMMENT '兜底策略（DEFAULT_GROUP/TAG_BASED）',
    `default_group_code` varchar(30) NOT NULL DEFAULT 'Tier D' COMMENT '默认分组（最保守兜底）',
    `group_code_985` varchar(30) DEFAULT NULL COMMENT '985 对应分组（TAG_BASED）',
    `group_code_211` varchar(30) DEFAULT NULL COMMENT '211 对应分组（TAG_BASED）',
    `group_code_double_first_class` varchar(30) DEFAULT NULL COMMENT '双一流对应分组（TAG_BASED）',
    `group_code_other_public` varchar(30) DEFAULT NULL COMMENT '其它公办本科对应分组（TAG_BASED）',
    `group_code_private` varchar(30) DEFAULT NULL COMMENT '民办对应分组（TAG_BASED）',
    `group_code_independent` varchar(30) DEFAULT NULL COMMENT '独立学院对应分组（TAG_BASED）',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`target_school_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '英国目标学校分组兜底配置';

CREATE TABLE IF NOT EXISTS `uk_school_requirement` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `target_school_id` bigint NOT NULL COMMENT '英国目标学校ID（schools.id）',
    `group_code` varchar(30) NOT NULL COMMENT '分组代码（与 uk_school_undergrad_group 一致）',
    `degree_req` varchar(30) DEFAULT NULL COMMENT '学位要求（2:1/2:2/High 2:1 等，可空表示通用）',
    `major_category` varchar(60) DEFAULT NULL COMMENT '专业大类（ECS/Business/Engineering 等，可空表示通用）',
    `required_score` double DEFAULT NULL COMMENT '最低均分/门槛（可空；若 is_reject=1 则可为空）',
    `is_reject` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否直接拒绝（1=拒绝，0=按 required_score 判断）',
    `version` varchar(50) DEFAULT NULL COMMENT '版本（可选）',
    `effective_from` datetime DEFAULT NULL COMMENT '生效开始时间（可选）',
    `effective_to` datetime DEFAULT NULL COMMENT '生效结束时间（可选）',
    `priority` int DEFAULT 0 COMMENT '优先级（数值越大越优先）',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_uk_req_target` (`target_school_id`),
    KEY `idx_uk_req_group` (`group_code`),
    KEY `idx_uk_req_degree` (`degree_req`),
    KEY `idx_uk_req_category` (`major_category`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '英国目标学校门槛规则矩阵';

ALTER TABLE `undergrad_schools`
MODIFY `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
MODIFY `canonical_name` varchar(255) NOT NULL COMMENT '标准中文名（canonical）',
MODIFY `canonical_english_name` varchar(255) DEFAULT NULL COMMENT '标准英文名（canonical，可选）',
MODIFY `canonical_key` varchar(255) NOT NULL COMMENT '标准化唯一键（用于去重/导入对齐）',
MODIFY `is_985` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否985（用于兜底分组）',
MODIFY `is_211` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否211（用于兜底分组）',
MODIFY `is_double_first_class` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否双一流（用于兜底分组）',
MODIFY `school_type` varchar(40) DEFAULT NULL COMMENT '学校类型（public/private/independent 或 公办/民办/独立学院）',
MODIFY `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
MODIFY `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
COMMENT = '中国本科院校标准化（全局）';

ALTER TABLE `undergrad_school_aliases`
MODIFY `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
MODIFY `undergrad_school_id` bigint NOT NULL COMMENT '本科院校标准ID（undergrad_schools.id）',
MODIFY `alias` varchar(255) NOT NULL COMMENT '别名原文（输入值）',
MODIFY `alias_norm` varchar(255) NOT NULL COMMENT '别名归一化（用于匹配）',
MODIFY `lang` varchar(10) DEFAULT NULL COMMENT '语言（zh/en等，可选）',
MODIFY `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
MODIFY `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
COMMENT = '本科院校别名表（用于归一化）';

ALTER TABLE `uk_school_undergrad_group`
MODIFY `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
MODIFY `target_school_id` bigint NOT NULL COMMENT '英国目标学校ID（schools.id）',
MODIFY `undergrad_school_id` bigint NOT NULL COMMENT '中国本科院校标准ID（undergrad_schools.id）',
MODIFY `group_code` varchar(30) NOT NULL COMMENT '分组代码（Group1/2/3/4 或 TierA/B/C/D 等）',
MODIFY `version` varchar(50) DEFAULT NULL COMMENT '版本（可选）',
MODIFY `effective_from` datetime DEFAULT NULL COMMENT '生效开始时间（可选）',
MODIFY `effective_to` datetime DEFAULT NULL COMMENT '生效结束时间（可选）',
MODIFY `priority` int DEFAULT 0 COMMENT '优先级（数值越大越优先）',
MODIFY `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
MODIFY `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
COMMENT = '按英国目标学校维度的本科院校分组映射';

ALTER TABLE `uk_school_group_fallback`
MODIFY `target_school_id` bigint NOT NULL COMMENT '英国目标学校ID（schools.id）',
MODIFY `fallback_strategy` varchar(30) NOT NULL DEFAULT 'DEFAULT_GROUP' COMMENT '兜底策略（DEFAULT_GROUP/TAG_BASED）',
MODIFY `default_group_code` varchar(30) NOT NULL DEFAULT 'Tier D' COMMENT '默认分组（最保守兜底）',
MODIFY `group_code_985` varchar(30) DEFAULT NULL COMMENT '985 对应分组（TAG_BASED）',
MODIFY `group_code_211` varchar(30) DEFAULT NULL COMMENT '211 对应分组（TAG_BASED）',
MODIFY `group_code_double_first_class` varchar(30) DEFAULT NULL COMMENT '双一流对应分组（TAG_BASED）',
MODIFY `group_code_other_public` varchar(30) DEFAULT NULL COMMENT '其它公办本科对应分组（TAG_BASED）',
MODIFY `group_code_private` varchar(30) DEFAULT NULL COMMENT '民办对应分组（TAG_BASED）',
MODIFY `group_code_independent` varchar(30) DEFAULT NULL COMMENT '独立学院对应分组（TAG_BASED）',
MODIFY `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
MODIFY `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
COMMENT = '英国目标学校分组兜底配置';

ALTER TABLE `uk_school_requirement`
MODIFY `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
MODIFY `target_school_id` bigint NOT NULL COMMENT '英国目标学校ID（schools.id）',
MODIFY `group_code` varchar(30) NOT NULL COMMENT '分组代码（与 uk_school_undergrad_group 一致）',
MODIFY `degree_req` varchar(30) DEFAULT NULL COMMENT '学位要求（2:1/2:2/High 2:1 等，可空表示通用）',
MODIFY `major_category` varchar(60) DEFAULT NULL COMMENT '专业大类（ECS/Business/Engineering 等，可空表示通用）',
MODIFY `required_score` double DEFAULT NULL COMMENT '最低均分/门槛（可空；若 is_reject=1 则可为空）',
MODIFY `is_reject` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否直接拒绝（1=拒绝，0=按 required_score 判断）',
MODIFY `version` varchar(50) DEFAULT NULL COMMENT '版本（可选）',
MODIFY `effective_from` datetime DEFAULT NULL COMMENT '生效开始时间（可选）',
MODIFY `effective_to` datetime DEFAULT NULL COMMENT '生效结束时间（可选）',
MODIFY `priority` int DEFAULT 0 COMMENT '优先级（数值越大越优先）',
MODIFY `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
MODIFY `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
COMMENT = '英国目标学校门槛规则矩阵';

INSERT INTO
    uk_school_requirement (
        target_school_id,
        group_code,
        degree_req,
        major_category,
        required_score,
        is_reject,
        version,
        priority
    )
SELECT s.id, 'Tier A', '2:1', NULL, 70.0, 0, 'SOUTHAMPTON_2026_PGT', 100
FROM (
        SELECT id
        FROM schools
        WHERE
            name = '南安普顿大学'
            OR english_name = 'University of Southampton'
        ORDER BY id
        LIMIT 1
    ) s
WHERE
    NOT EXISTS (
        SELECT 1
        FROM uk_school_requirement r
        WHERE
            r.target_school_id = s.id
            AND r.group_code = 'Tier A'
            AND r.degree_req <=> '2:1'
            AND r.major_category <=> NULL
    );

INSERT INTO
    uk_school_requirement (
        target_school_id,
        group_code,
        degree_req,
        major_category,
        required_score,
        is_reject,
        version,
        priority
    )
SELECT s.id, 'Tier B', '2:1', NULL, 75.0, 0, 'SOUTHAMPTON_2026_PGT', 100
FROM (
        SELECT id
        FROM schools
        WHERE
            name = '南安普顿大学'
            OR english_name = 'University of Southampton'
        ORDER BY id
        LIMIT 1
    ) s
WHERE
    NOT EXISTS (
        SELECT 1
        FROM uk_school_requirement r
        WHERE
            r.target_school_id = s.id
            AND r.group_code = 'Tier B'
            AND r.degree_req <=> '2:1'
            AND r.major_category <=> NULL
    );

INSERT INTO
    uk_school_requirement (
        target_school_id,
        group_code,
        degree_req,
        major_category,
        required_score,
        is_reject,
        version,
        priority
    )
SELECT s.id, 'Tier C', '2:1', NULL, 80.0, 0, 'SOUTHAMPTON_2026_PGT', 100
FROM (
        SELECT id
        FROM schools
        WHERE
            name = '南安普顿大学'
            OR english_name = 'University of Southampton'
        ORDER BY id
        LIMIT 1
    ) s
WHERE
    NOT EXISTS (
        SELECT 1
        FROM uk_school_requirement r
        WHERE
            r.target_school_id = s.id
            AND r.group_code = 'Tier C'
            AND r.degree_req <=> '2:1'
            AND r.major_category <=> NULL
    );

INSERT INTO
    uk_school_requirement (
        target_school_id,
        group_code,
        degree_req,
        major_category,
        required_score,
        is_reject,
        version,
        priority
    )
SELECT s.id, 'Tier D', '2:1', NULL, 88.0, 0, 'SOUTHAMPTON_2026_PGT', 100
FROM (
        SELECT id
        FROM schools
        WHERE
            name = '南安普顿大学'
            OR english_name = 'University of Southampton'
        ORDER BY id
        LIMIT 1
    ) s
WHERE
    NOT EXISTS (
        SELECT 1
        FROM uk_school_requirement r
        WHERE
            r.target_school_id = s.id
            AND r.group_code = 'Tier D'
            AND r.degree_req <=> '2:1'
            AND r.major_category <=> NULL
    );

INSERT INTO
    uk_school_requirement (
        target_school_id,
        group_code,
        degree_req,
        major_category,
        required_score,
        is_reject,
        version,
        priority
    )
SELECT s.id, 'Tier A', '2:2', NULL, 65.0, 0, 'SOUTHAMPTON_2026_PGT', 90
FROM (
        SELECT id
        FROM schools
        WHERE
            name = '南安普顿大学'
            OR english_name = 'University of Southampton'
        ORDER BY id
        LIMIT 1
    ) s
WHERE
    NOT EXISTS (
        SELECT 1
        FROM uk_school_requirement r
        WHERE
            r.target_school_id = s.id
            AND r.group_code = 'Tier A'
            AND r.degree_req <=> '2:2'
            AND r.major_category <=> NULL
    );

INSERT INTO
    uk_school_requirement (
        target_school_id,
        group_code,
        degree_req,
        major_category,
        required_score,
        is_reject,
        version,
        priority
    )
SELECT s.id, 'Tier B', '2:2', NULL, 70.0, 0, 'SOUTHAMPTON_2026_PGT', 90
FROM (
        SELECT id
        FROM schools
        WHERE
            name = '南安普顿大学'
            OR english_name = 'University of Southampton'
        ORDER BY id
        LIMIT 1
    ) s
WHERE
    NOT EXISTS (
        SELECT 1
        FROM uk_school_requirement r
        WHERE
            r.target_school_id = s.id
            AND r.group_code = 'Tier B'
            AND r.degree_req <=> '2:2'
            AND r.major_category <=> NULL
    );

INSERT INTO
    uk_school_requirement (
        target_school_id,
        group_code,
        degree_req,
        major_category,
        required_score,
        is_reject,
        version,
        priority
    )
SELECT s.id, 'Tier C', '2:2', NULL, 75.0, 0, 'SOUTHAMPTON_2026_PGT', 90
FROM (
        SELECT id
        FROM schools
        WHERE
            name = '南安普顿大学'
            OR english_name = 'University of Southampton'
        ORDER BY id
        LIMIT 1
    ) s
WHERE
    NOT EXISTS (
        SELECT 1
        FROM uk_school_requirement r
        WHERE
            r.target_school_id = s.id
            AND r.group_code = 'Tier C'
            AND r.degree_req <=> '2:2'
            AND r.major_category <=> NULL
    );

INSERT INTO
    uk_school_requirement (
        target_school_id,
        group_code,
        degree_req,
        major_category,
        required_score,
        is_reject,
        version,
        priority
    )
SELECT s.id, 'Tier D', '2:2', NULL, 83.0, 0, 'SOUTHAMPTON_2026_PGT', 90
FROM (
        SELECT id
        FROM schools
        WHERE
            name = '南安普顿大学'
            OR english_name = 'University of Southampton'
        ORDER BY id
        LIMIT 1
    ) s
WHERE
    NOT EXISTS (
        SELECT 1
        FROM uk_school_requirement r
        WHERE
            r.target_school_id = s.id
            AND r.group_code = 'Tier D'
            AND r.degree_req <=> '2:2'
            AND r.major_category <=> NULL
    );

INSERT INTO
    uk_school_requirement (
        target_school_id,
        group_code,
        degree_req,
        major_category,
        required_score,
        is_reject,
        version,
        priority
    )
SELECT s.id, 'Tier D', NULL, 'ECS_ENGINEERING', NULL, 1, 'SOUTHAMPTON_2026_PGT', 200
FROM (
        SELECT id
        FROM schools
        WHERE
            name = '南安普顿大学'
            OR english_name = 'University of Southampton'
        ORDER BY id
        LIMIT 1
    ) s
WHERE
    NOT EXISTS (
        SELECT 1
        FROM uk_school_requirement r
        WHERE
            r.target_school_id = s.id
            AND r.group_code = 'Tier D'
            AND r.degree_req <=> NULL
            AND r.major_category <=> 'ECS_ENGINEERING'
    );

INSERT INTO
    uk_school_requirement (
        target_school_id,
        group_code,
        degree_req,
        major_category,
        required_score,
        is_reject,
        version,
        priority
    )
SELECT s.id, 'Tier D', NULL, NULL, 75.0, 0, 'UK_DEFAULT', 1
FROM schools s
WHERE (
        s.country = '英国'
        OR s.country_code IN ('UK', 'GB')
    )
    AND (
        s.is_deleted = 0
        OR s.is_deleted IS NULL
    )
    AND (
        s.status = 1
        OR s.status IS NULL
    )
    AND NOT EXISTS (
        SELECT 1
        FROM uk_school_requirement r
        WHERE
            r.target_school_id = s.id
            AND r.group_code = 'Tier D'
            AND r.degree_req <=> NULL
            AND r.major_category <=> NULL
    );