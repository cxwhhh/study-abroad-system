package com.study.service.matching.dto;

import java.util.List;

/**
 * 匹配请求DTO
 */
public class MatchingRequest {
    
    private String region; // 目标地区
    private StudentInfoDTO studentInfo; // 学生信息
    private List<String> targetSchools; // 目标学校列表（可选）
    private List<String> targetMajors; // 目标专业列表（可选）
    
    // Getters and Setters
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    public StudentInfoDTO getStudentInfo() {
        return studentInfo;
    }
    
    public void setStudentInfo(StudentInfoDTO studentInfo) {
        this.studentInfo = studentInfo;
    }
    
    public List<String> getTargetSchools() {
        return targetSchools;
    }
    
    public void setTargetSchools(List<String> targetSchools) {
        this.targetSchools = targetSchools;
    }
    
    public List<String> getTargetMajors() {
        return targetMajors;
    }
    
    public void setTargetMajors(List<String> targetMajors) {
        this.targetMajors = targetMajors;
    }
    
    /**
     * 学生信息DTO
     */
    public static class StudentInfoDTO {
        private Long userId; // 用户ID
        private String name; // 姓名
        private String undergraduateSchool; // 本科院校
        private String undergraduateMajor; // 本科专业
        private Double gpa; // GPA
        private String gpaScale; // GPA制度 (4.0, 5.0, 100)
        private String targetMajor; // 目标专业
        private String targetDegree; // 目标学位 (硕士/博士)
        private Integer graduationYear; // 毕业年份
        private String languageTest; // 语言考试类型 (IELTS/TOEFL)
        private Double languageScore; // 语言考试分数
        private String gmatGre; // GMAT/GRE考试类型
        private Integer gmatGreScore; // GMAT/GRE分数
        private String workExperience; // 工作经验
        private String researchExperience; // 研究经验
        private String internshipExperience; // 实习经验
        private String awards; // 获奖情况
        private String publications; // 发表论文
        private String personalStatement; // 个人陈述要点
        private String specialRequirements; // 特殊要求
        
        // Getters and Setters
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getUndergraduateSchool() {
            return undergraduateSchool;
        }
        
        public void setUndergraduateSchool(String undergraduateSchool) {
            this.undergraduateSchool = undergraduateSchool;
        }
        
        public String getUndergraduateMajor() {
            return undergraduateMajor;
        }
        
        public void setUndergraduateMajor(String undergraduateMajor) {
            this.undergraduateMajor = undergraduateMajor;
        }
        
        public Double getGpa() {
            return gpa;
        }
        
        public void setGpa(Double gpa) {
            this.gpa = gpa;
        }
        
        public String getGpaScale() {
            return gpaScale;
        }
        
        public void setGpaScale(String gpaScale) {
            this.gpaScale = gpaScale;
        }
        
        public String getTargetMajor() {
            return targetMajor;
        }
        
        public void setTargetMajor(String targetMajor) {
            this.targetMajor = targetMajor;
        }
        
        public String getTargetDegree() {
            return targetDegree;
        }
        
        public void setTargetDegree(String targetDegree) {
            this.targetDegree = targetDegree;
        }
        
        public Integer getGraduationYear() {
            return graduationYear;
        }
        
        public void setGraduationYear(Integer graduationYear) {
            this.graduationYear = graduationYear;
        }
        
        public String getLanguageTest() {
            return languageTest;
        }
        
        public void setLanguageTest(String languageTest) {
            this.languageTest = languageTest;
        }
        
        public Double getLanguageScore() {
            return languageScore;
        }
        
        public void setLanguageScore(Double languageScore) {
            this.languageScore = languageScore;
        }
        
        public String getGmatGre() {
            return gmatGre;
        }
        
        public void setGmatGre(String gmatGre) {
            this.gmatGre = gmatGre;
        }
        
        public Integer getGmatGreScore() {
            return gmatGreScore;
        }
        
        public void setGmatGreScore(Integer gmatGreScore) {
            this.gmatGreScore = gmatGreScore;
        }
        
        public String getWorkExperience() {
            return workExperience;
        }
        
        public void setWorkExperience(String workExperience) {
            this.workExperience = workExperience;
        }
        
        public String getResearchExperience() {
            return researchExperience;
        }
        
        public void setResearchExperience(String researchExperience) {
            this.researchExperience = researchExperience;
        }
        
        public String getInternshipExperience() {
            return internshipExperience;
        }
        
        public void setInternshipExperience(String internshipExperience) {
            this.internshipExperience = internshipExperience;
        }
        
        public String getAwards() {
            return awards;
        }
        
        public void setAwards(String awards) {
            this.awards = awards;
        }
        
        public String getPublications() {
            return publications;
        }
        
        public void setPublications(String publications) {
            this.publications = publications;
        }
        
        public String getPersonalStatement() {
            return personalStatement;
        }
        
        public void setPersonalStatement(String personalStatement) {
            this.personalStatement = personalStatement;
        }
        
        public String getSpecialRequirements() {
            return specialRequirements;
        }
        
        public void setSpecialRequirements(String specialRequirements) {
            this.specialRequirements = specialRequirements;
        }
    }
}