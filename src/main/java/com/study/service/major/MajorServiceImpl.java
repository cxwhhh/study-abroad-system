package com.study.service.major;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 专业服务实现类
 */
@Service
public class MajorServiceImpl implements MajorService {

    @Autowired
    private MajorMapper majorMapper;
    
    @Override
    public List<Major> getAllMajors() {
        return majorMapper.findAll();
    }

    @Override
    public Major getMajorById(Long id) {
        return majorMapper.findById(id);
    }

    @Override
    public List<Major> getMajorsByFacultyId(Long facultyId) {
        return majorMapper.findByFacultyId(facultyId);
    }

    @Override
    public List<Major> getMajorsBySchoolId(Long schoolId) {
        return majorMapper.findBySchoolId(schoolId);
    }

    @Override
    public Major addMajor(Major major) {
        majorMapper.insert(major);
        return major;
    }

    @Override
    public boolean updateMajor(Major major) {
        return majorMapper.update(major) > 0;
    }

    @Override
    public boolean deleteMajor(Long id) {
        return majorMapper.deleteById(id) > 0;
    }
} 