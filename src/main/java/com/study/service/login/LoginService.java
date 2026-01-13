package com.study.service.login;

import com.study.service.user.User;

public interface LoginService {
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    User login(String username, String password);

    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param email 邮箱
     * @return 用户信息
     */
    User register(String username, String password, String email);

    /**
     * 根据用户ID获取学生ID
     * @param userId 用户ID
     * @return 学生ID，如果不存在返回 null
     */
    Long getStudentIdByUserId(Long userId);
} 