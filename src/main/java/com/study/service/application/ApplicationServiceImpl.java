package com.study.service.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 申请服务实现类
 */
@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationMapper applicationMapper;
    
    @Override
    public List<Application> getAllApplications() {
        return applicationMapper.findAll();
    }

    @Override
    public Application getApplicationById(Long id) {
        return applicationMapper.findById(id);
    }

    @Override
    public List<Application> getApplicationsByUserId(Long userId) {
        return applicationMapper.findByUserId(userId);
    }

    @Override
    public List<Application> getApplicationsBySchoolId(Long schoolId) {
        return applicationMapper.findBySchoolId(schoolId);
    }

    @Override
    public List<Application> getApplicationsByMajorId(Long majorId) {
        return applicationMapper.findByMajorId(majorId);
    }

    @Override
    public List<Application> getApplicationsByStatus(String status) {
        return applicationMapper.findByStatus(status);
    }

    @Override
    public Application addApplication(Application application) {
        applicationMapper.insert(application);
        return application;
    }

    @Override
    public boolean updateApplication(Application application) {
        return applicationMapper.update(application) > 0;
    }

    @Override
    public boolean deleteApplication(Long id) {
        return applicationMapper.deleteById(id) > 0;
    }
} 