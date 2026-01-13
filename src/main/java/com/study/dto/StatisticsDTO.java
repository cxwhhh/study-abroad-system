package com.study.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员用户统计数据传输对象
 */
@Data
@NoArgsConstructor
public class StatisticsDTO {
    
    private long totalUsers;
    private long activeUsers;
    private long adminUsers;
    private long superAdminUsers; // 超级管理员
    private long normalAdminUsers; // 普通管理员
    private long consultantUsers; // 咨询顾问
    private long financialUsers; // 财务人员
    private long dataAnalystUsers; // 数据分析师
    private long inactiveUsers;
    private long teacherUsers;
    
    // 添加包含所有字段的新构造函数
    public StatisticsDTO(long totalUsers, long activeUsers, long inactiveUsers, long adminUsers,
                        long superAdminUsers, long normalAdminUsers, long consultantUsers,
                        long financialUsers, long dataAnalystUsers, long teacherUsers) {
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.inactiveUsers = inactiveUsers;
        this.adminUsers = adminUsers;
        this.superAdminUsers = superAdminUsers;
        this.normalAdminUsers = normalAdminUsers;
        this.consultantUsers = consultantUsers;
        this.financialUsers = financialUsers;
        this.dataAnalystUsers = dataAnalystUsers;
        this.teacherUsers = teacherUsers;
    }
} 