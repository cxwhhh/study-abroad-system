package com.study.service.students;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.service.user.UserMapper;

import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DataSource dataSource;

    private final AtomicBoolean studentsGpaColumnEnsured = new AtomicBoolean(false);

    // 从properties文件中读取数据库配置
    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Override
    public Student getStudentByUserId(Long userId) {
        if (userId == null) {
            log.warn("用户ID为空，不获取学生信息");
            return null;
        }

        try {
            log.info("获取用户ID为 {} 的学生信息", userId);
            Student student = studentMapper.getStudentByUserId(userId);
            if (student == null) {
                log.info("未找到用户ID为 {} 的学生信息", userId);
            }
            return student;
        } catch (Exception e) {
            log.error("获取学生信息失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取学生信息失败", e);
        }
    }

    @Override
    @Transactional
    public boolean updateStudent(Student student) {
        try {
            log.info("更新学生信息: {}", student);

            // 检查学生ID是否存在
            if (student.getId() == null) {
                log.error("更新学生信息失败: 学生ID不能为空");
                return false;
            }

            normalizeStudentForStorage(student);

            // 设置更新时间
            student.setUpdateTime(new Date());

            // 执行更新
            int result = studentMapper.updateStudent(student);
            log.info("更新学生信息结果: 影响的行数 = {}", result);

            return result > 0;
        } catch (Exception e) {
            log.error("更新学生信息失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public Student syncStudentInfo(Student student) {
        try {
            log.info("开始同步学生信息到数据库: {}", student);

            if (student == null || student.getUserId() == null) {
                throw new RuntimeException("用户ID不能为空");
            }

            normalizeStudentForStorage(student);

            // 检查是否存在记录
            Student existingStudent = studentMapper.getStudentByUserId(student.getUserId());

            if (existingStudent == null) {
                // 不存在则插入新记录
                studentMapper.insert(student);
                log.info("新增学生信息成功");
                return student;
            } else {
                // 存在则更新记录
                // 保留数据库中的一些重要字段
                mergeMissingFields(student, existingStudent);
                student.setId(existingStudent.getId());
                student.setCreateTime(existingStudent.getCreateTime());
                studentMapper.updateStudent(student);
                log.info("更新学生信息成功");
                return student;
            }
        } catch (Exception e) {
            log.error("同步学生信息失败: {}", e.getMessage(), e);
            Throwable root = e;
            while (root.getCause() != null && root.getCause() != root) {
                root = root.getCause();
            }
            String msg = root.getMessage();
            if (msg == null || msg.isBlank()) {
                msg = e.getMessage();
            }
            if (msg == null || msg.isBlank()) {
                msg = "同步学生信息失败";
            }
            throw new RuntimeException(msg, e);
        }
    }

    private static void mergeMissingFields(Student target, Student source) {
        if (target == null || source == null) {
            return;
        }

        if (isBlank(target.getName()))
            target.setName(source.getName());
        if (isBlank(target.getEnglishName()))
            target.setEnglishName(source.getEnglishName());
        if (isBlank(target.getGender()))
            target.setGender(source.getGender());
        if (target.getBirthDate() == null)
            target.setBirthDate(source.getBirthDate());
        if (isBlank(target.getNationality()))
            target.setNationality(source.getNationality());
        if (isBlank(target.getPassportNo()))
            target.setPassportNo(source.getPassportNo());
        if (isBlank(target.getPhone()))
            target.setPhone(source.getPhone());
        if (isBlank(target.getEmail()))
            target.setEmail(source.getEmail());
        if (isBlank(target.getWechat()))
            target.setWechat(source.getWechat());
        if (isBlank(target.getCurrentSchool()))
            target.setCurrentSchool(source.getCurrentSchool());
        if (isBlank(target.getMajor()))
            target.setMajor(source.getMajor());

        if (target.getGpa() == null)
            target.setGpa(source.getGpa());
        if (isBlank(target.getGpaScale()))
            target.setGpaScale(source.getGpaScale());
        if (target.getToefl() == null)
            target.setToefl(source.getToefl());
        if (target.getIelts() == null)
            target.setIelts(source.getIelts());
        if (target.getGre() == null)
            target.setGre(source.getGre());
        if (target.getGmat() == null)
            target.setGmat(source.getGmat());

        if (isBlank(target.getApplicationStatus()))
            target.setApplicationStatus(source.getApplicationStatus());
        if (target.getApplicationProgress() == null)
            target.setApplicationProgress(source.getApplicationProgress());
        if (isBlank(target.getCurrentStep()))
            target.setCurrentStep(source.getCurrentStep());
        if (isBlank(target.getTargetCountries()))
            target.setTargetCountries(source.getTargetCountries());
        if (target.getAdvisorId() == null)
            target.setAdvisorId(source.getAdvisorId());
        if (isBlank(target.getNotes()))
            target.setNotes(source.getNotes());
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    @Override
    public StudentGPA getStudentGPA(Long userId) {
        try {
            log.info("获取用户ID为 {} 的GPA信息", userId);
            return studentMapper.getStudentGPA(userId);
        } catch (Exception e) {
            log.error("获取学生GPA信息失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取学生GPA信息失败", e);
        }
    }

    @Override
    public List<Student> getAllStudents() {
        try {
            log.info("获取所有学生列表");
            return studentMapper.getAllStudents();
        } catch (Exception e) {
            log.error("获取所有学生列表失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取所有学生列表失败", e);
        }
    }

    @Override
    @Transactional
    public Student addStudent(Student student) {
        try {
            log.info("添加新学生: {}", student);

            // 验证用户ID是否存在于users表中
            if (student.getUserId() != null) {
                com.study.service.user.User user = userMapper.findById(student.getUserId());
                if (user == null) {
                    log.error("添加学生失败: 用户ID {} 不存在", student.getUserId());
                    throw new RuntimeException("用户ID不存在，请确认输入正确的用户ID");
                }
            }

            // 设置创建时间和更新时间
            if (student.getCreateTime() == null) {
                student.setCreateTime(new Date());
            }

            normalizeStudentForStorage(student);

            student.setUpdateTime(new Date());

            // 插入新学生记录
            studentMapper.insert(student);

            log.info("添加学生成功，ID: {}", student.getId());
            return student;
        } catch (Exception e) {
            log.error("添加学生失败: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        try {
            log.info("删除学生，ID: {}", id);
            int result = studentMapper.deleteById(id);

            if (result > 0) {
                log.info("删除学生成功，ID: {}", id);
            } else {
                log.warn("未找到要删除的学生，ID: {}", id);
                throw new RuntimeException("未找到要删除的学生");
            }
        } catch (Exception e) {
            log.error("删除学生失败: {}", e.getMessage(), e);
            throw new RuntimeException("删除学生失败", e);
        }
    }

    @Override
    public Student getStudentById(Long id) {
        try {
            log.info("根据ID获取学生信息, ID: {}", id);

            // 直接从数据库查询学生记录
            // 注意：这里假设StudentMapper中有getStudentById方法
            // 如果没有，需要添加该方法
            Student student = studentMapper.getStudentById(id);

            if (student == null) {
                log.warn("未找到ID为 {} 的学生信息", id);
            }

            return student;
        } catch (Exception e) {
            log.error("获取学生信息失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取学生信息失败", e);
        }
    }

    private void normalizeStudentForStorage(Student student) {
        if (student == null) {
            return;
        }
        ensureStudentsGpaColumnCapacity();
        validateGpaForStorage(student);
    }

    private void ensureStudentsGpaColumnCapacity() {
        if (!studentsGpaColumnEnsured.compareAndSet(false, true)) {
            return;
        }

        try (Connection conn = dataSource.getConnection()) {
            Integer precision = null;
            Integer scale = null;

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT NUMERIC_PRECISION, NUMERIC_SCALE " +
                            "FROM information_schema.COLUMNS " +
                            "WHERE TABLE_SCHEMA = DATABASE() " +
                            "AND TABLE_NAME = 'students' " +
                            "AND COLUMN_NAME = 'gpa'")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        precision = rs.getObject(1) == null ? null : rs.getInt(1);
                        scale = rs.getObject(2) == null ? null : rs.getInt(2);
                    }
                }
            }

            boolean needsAlter = precision == null || scale == null || precision < 5 || scale < 2;
            if (!needsAlter) {
                return;
            }

            try (PreparedStatement alter = conn.prepareStatement(
                    "ALTER TABLE students MODIFY COLUMN gpa DECIMAL(5,2) NULL")) {
                alter.executeUpdate();
            }
        } catch (SQLException e) {
            log.warn("检查/修复 students.gpa 字段精度失败: {}", e.getMessage(), e);
        }
    }

    private void validateGpaForStorage(Student student) {
        Double gpa = student.getGpa();
        if (gpa == null) {
            return;
        }

        if (gpa < 0.0)
            student.setGpa(0.0);

        String scaleRaw = student.getGpaScale();
        String scale = scaleRaw == null ? null : scaleRaw.trim();
        if (scale == null || scale.isEmpty() || "0".equals(scale)) {
            if (student.getGpa() > 999.99)
                student.setGpa(999.99);
            return;
        }

        Double scaleValue;
        try {
            scaleValue = Double.parseDouble(scale);
        } catch (NumberFormatException e) {
            if (student.getGpa() > 999.99)
                student.setGpa(999.99);
            return;
        }

        if (scaleValue <= 0.0) {
            if (student.getGpa() > 999.99)
                student.setGpa(999.99);
            return;
        }

        if (student.getGpa() > scaleValue) {
            student.setGpa(scaleValue);
        }

        if (student.getGpa() > 999.99) {
            student.setGpa(999.99);
        }
    }
}
