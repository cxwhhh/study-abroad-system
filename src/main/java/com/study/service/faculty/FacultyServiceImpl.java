package com.study.service.faculty;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.study.service.major.Major;
import com.study.service.major.MajorMapper;
import com.study.service.schools.School;
import com.study.service.schools.SchoolMapper;

/**
 * 学院服务实现类
 */
@Service
public class FacultyServiceImpl implements FacultyService {

    private static final Logger logger = LoggerFactory.getLogger(FacultyServiceImpl.class);

    @Autowired
    private FacultyMapper facultyMapper;
    
    @Autowired
    private MajorMapper majorMapper;
    
    @Autowired
    private SchoolMapper schoolMapper;
    
    @Override
    public List<Faculty> getAllFaculties() {
        return facultyMapper.findAll();
    }

    @Override
    public Faculty getFacultyById(Long id) {
        return facultyMapper.findById(id);
    }

    @Override
    public List<Faculty> getFacultiesBySchoolId(Long schoolId) {
        logger.info("开始获取院校ID为 {} 的学院列表", schoolId);
        
        // 首先检查学校是否存在
        School school = schoolMapper.findById(schoolId);
        if (school == null) {
            logger.warn("院校ID {} 不存在", schoolId);
            return List.of(); // 返回空列表
        }
        
        // 获取该学校的所有学院
        List<Faculty> faculties = facultyMapper.findBySchoolId(schoolId);
        logger.info("找到 {} 个学院", faculties.size());
        
        // 为每个学院加载专业信息
        for (Faculty faculty : faculties) {
            logger.info("开始获取学院 {} (ID: {}) 的专业列表", faculty.getName(), faculty.getId());
            List<Major> majors = majorMapper.findByFacultyId(faculty.getId());
            faculty.setMajors(majors);
            logger.info("学院 {} 有 {} 个专业", faculty.getName(), majors.size());
        }
        
        return faculties;
    }

    @Override
    public Faculty addFaculty(Faculty faculty) {
        // 检查关联的学校是否存在
        School school = schoolMapper.findById(faculty.getSchoolId());
        if (school == null) {
            throw new IllegalArgumentException("关联的院校不存在");
        }
        
        facultyMapper.insert(faculty);
        return faculty;
    }

    @Override
    public boolean updateFaculty(Faculty faculty) {
        // 检查关联的学校是否存在
        School school = schoolMapper.findById(faculty.getSchoolId());
        if (school == null) {
            throw new IllegalArgumentException("关联的院校不存在");
        }
        
        return facultyMapper.update(faculty) > 0;
    }

    @Override
    public boolean deleteFaculty(Long id) {
        return facultyMapper.deleteById(id) > 0;
    }
} 
