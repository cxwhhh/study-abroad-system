package com.study.service.applicationschool;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;

@Service
public class ApplicationSchoolServiceImpl implements ApplicationSchoolService {

    private final ApplicationSchoolMapper applicationSchoolMapper;
    private static final Logger log = LoggerFactory.getLogger(ApplicationSchoolServiceImpl.class);

    public ApplicationSchoolServiceImpl(ApplicationSchoolMapper applicationSchoolMapper) {
        this.applicationSchoolMapper = applicationSchoolMapper;
    }

    @Override
    public ApplicationSchool submit(ApplicationSchool applicationSchool) {
        try {
            // 将 LocalDateTime 转换为 Date
            Date now = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
            applicationSchool.setCreateTime(now);
            applicationSchool.setUpdateTime(now); // 设置更新时间

            if (applicationSchool.getApplicationStatus() == null) {
                applicationSchool.setApplicationStatus("submitted");
            }

            System.out.println("Inserting application: " + applicationSchool);

            applicationSchoolMapper.insert(applicationSchool);
            return applicationSchool;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("保存申请失败: " + e.getMessage());
        }
    }

    @Override
    public List<ApplicationSchool> getSubmittedApplications(Long userId, Long studentId, String country) {
        log.info("Getting submitted applications for userId: {}, studentId: {}, country: {}", userId, studentId,
                country);

        try {
            List<ApplicationSchool> result;

            // 根据传入的参数进行不同的查询
            if (studentId != null) {
                // 如果有学生ID，按学生ID查询
                log.info("Querying by studentId: {}", studentId);
                result = applicationSchoolMapper.findByStudentId(studentId);
                log.info("Found {} applications for studentId: {}", result.size(), studentId);
            } else if (userId == null) {
                // 如果是管理员视图（userId为空），返回所有申请
                if (country != null && !country.isEmpty()) {
                    log.info("Admin view: Querying all applications with country filter: {}", country);
                    result = applicationSchoolMapper.findByUserIdAndCountry(null, country);
                } else {
                    // 当userId为空且country为空时，返回所有申请
                    log.info("Admin view: Querying all applications without filters");
                    result = applicationSchoolMapper.findByUserId(null);
                }
                log.info("Admin view: Found {} applications", result.size());
            } else {
                // 普通用户视图，根据userId过滤
                if (country != null && !country.isEmpty()) {
                    log.info("User view: Querying applications for userId: {} with country filter: {}", userId,
                            country);
                    result = applicationSchoolMapper.findByUserIdAndCountry(userId, country);
                } else {
                    log.info("User view: Querying applications for userId: {}", userId);
                    result = applicationSchoolMapper.findByUserId(userId);
                }
                log.info("User view: Found {} applications for userId: {}", result.size(), userId);
            }

            // 打印找到的每个申请的关键信息
            for (ApplicationSchool app : result) {
                log.info("Application found - ID: {}, StudentID: {}, StudentName: {}, SchoolName: {}",
                        app.getId(), app.getStudentId(), app.getStudentName(), app.getSchoolName());
            }

            return result;
        } catch (Exception e) {
            log.error("Error getting submitted applications", e);
            return new ArrayList<>(); // 返回空列表而非抛出异常
        }
    }

    @Override
    public boolean updateApplicationStatus(Long id, String status) {
        return applicationSchoolMapper.updateStatus(id, status) > 0;
    }

    @Override
    public ApplicationSchool getApplicationDetail(Long id) {
        return applicationSchoolMapper.findDetailById(id);
    }

    @Override
    public boolean checkTablesJoined(Long userId) {
        return applicationSchoolMapper.checkJoinStatus(userId) > 0;
    }

    @Override
    public boolean updateVisaInfoProvided(Long id, Boolean visaInfoProvided) {
        try {
            log.info("Updating visa info for application id: {}, visaInfoProvided: {}", id, visaInfoProvided);
            int result = applicationSchoolMapper.updateVisaInfoProvided(id, visaInfoProvided);
            return result > 0;
        } catch (Exception e) {
            log.error("Error updating visa info for application id: " + id, e);
            return false;
        }
    }

    @Override
    public boolean deleteApplication(Long id) {
        try {
            log.info("Deleting application with id: {}", id);
            int result = applicationSchoolMapper.deleteById(id);
            return result > 0;
        } catch (Exception e) {
            log.error("Error deleting application with id: " + id, e);
            return false;
        }
    }
}
