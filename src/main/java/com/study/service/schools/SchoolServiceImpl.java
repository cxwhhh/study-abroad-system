package com.study.service.schools;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 院校服务实现类
 */
@Service
public class SchoolServiceImpl implements SchoolService {

    @Autowired
    private SchoolMapper schoolMapper;
    
    @Override
    public List<School> getAllSchools() {
        return schoolMapper.findAll();
    }

    @Override
    public School getSchoolById(Long id) {
        return schoolMapper.findById(id);
    }

    @Override
    public List<School> getSchoolsByCountry(String country) {
        return schoolMapper.findByCountry(country);
    }

    @Override
    public List<School> getSchoolsByType(String type) {
        return schoolMapper.findByType(type);
    }

    @Override
    @Transactional
    public School addSchool(School school) {
        // 设置创建和更新时间
        Date now = new Date();
        school.setCreatedAt(now);
        school.setUpdatedAt(now);
        
        // 插入数据
        schoolMapper.insert(school);
        return school;
    }

    @Override
    @Transactional
    public boolean updateSchool(School school) {
        // 设置更新时间
        school.setUpdatedAt(new Date());
        
        // 更新数据
        return schoolMapper.update(school) > 0;
    }

    @Override
    @Transactional
    public boolean deleteSchool(Long id) {
        return schoolMapper.deleteById(id) > 0;
    }

    @Override
    public List<School> searchSchools(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllSchools();
        }
        return schoolMapper.searchByKeyword(keyword.trim());
    }
} 