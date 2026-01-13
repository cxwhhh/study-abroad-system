package com.study.service.login;

import com.study.service.students.Student;
import com.study.service.students.StudentMapper;
import com.study.service.user.User;
import com.study.service.user.UserMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StudentMapper studentMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 更新最后登录时间
        user.setUpdatedAt(new Date());
        userMapper.updateLastLoginTime(user);
        
        // 清除密码
        user.setPassword(null);
        return user;
    }

    @Override
    @Transactional
    public User register(String username, String password, String email) {
        // 检查用户名是否已存在
        if (userMapper.findByUsername(username) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        log.info("开始创建新用户，用户名: {}, 邮箱: {}", username, email);

        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);  // 设置邮箱
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setStatus(1); // 1-正常
        
        log.info("准备插入用户，设置的email值: {}", user.getEmail());

        // 保存用户
        int result = userMapper.insert(user);
        log.info("用户保存结果: {}, 用户ID: {}, 保存后的email: {}", result, user.getId(), user.getEmail());
        
        // 重新查询确保数据准确
        if (user.getId() != null) {
            User savedUser = userMapper.findById(user.getId());
            if (savedUser != null) {
                // 清除密码
                savedUser.setPassword(null);
                log.info("返回最终用户数据，ID: {}, 用户名: {}, 邮箱: {}", 
                        savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
                return savedUser;
            }
        }
        
        // 清除密码
        user.setPassword(null);
        return user;
    }

    @Override
    public Long getStudentIdByUserId(Long userId) {
        Student student = studentMapper.getStudentByUserId(userId);
        return student != null ? student.getId() : null;
    }
} 