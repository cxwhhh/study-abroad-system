package com.study.service.applicationschool;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

@Data
public class ApplicationSchool {
    // 申请表主要字段
    private Long id;
    private Long userId;
    private Long schoolId; // 学校ID
    private Long majorId; // 专业ID
    private Long countryId; // 国家ID
    private Long studentId;
    private String schoolName; // 学校名称
    private String majorName; // 专业名称
    private String country; // 国家
    private String major; // 申请专业
    private String degree; // 学位类型
    private String code; // 课程代码
    private String entryDate; // 入学日期
    private String duration; // 学习时长
    private String campus; // 校区
    private String creditExemption; // 学分减免
    private String applicationStatus; // 申请状态
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "GMT+8")
    private Date submitTime; // 提交时间
    
    private String link; // 申请链接
    
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date applicationDeadline; // 申请截止日期
    
    private Boolean visaInfoProvided; // 是否已提供签证信息
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "GMT+8")
    private Date createTime; // 创建时间
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "GMT+8")
    private Date updateTime; // 更新时间

    // 关联的学生信息字段
    private String studentName;
    private String studentEmail;
    private String studentMajor;
    private String currentSchool;
    private String englishName;
    private String gender;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "GMT+8")
    private Date birthDate;
    
    private String nationality;
    private String passportNo;
    private String phone;
    private String wechat;
    private Double gpa;

    // 关联的用户信息字段
    private String userName; // 对应数据库的username字段
    private String userEmail; // 对应数据库的email字段
}