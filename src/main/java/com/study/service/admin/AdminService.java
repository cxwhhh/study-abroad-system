package com.study.service.admin;

public interface AdminService {
    /**
     * 管理员登录
     * @param username 用户名
     * @param password 密码
     * @return 管理员信息
     */
    AdminDTO login(String username, String password) throws Exception;
} 