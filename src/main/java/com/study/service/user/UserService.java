package com.study.service.user;

import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    /**
     * 获取当前登录用户的ID
     * @param request HTTP请求
     * @return 用户ID
     */
    Long getCurrentUserId(HttpServletRequest request);
    
    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息，如果不存在返回null
     */
    User getUserById(Long userId);
    
    /**
     * 获取当前登录用户的完整信息
     * @param request HTTP请求
     * @return 当前登录用户的信息，如果未登录返回null
     */
    User getCurrentUser(HttpServletRequest request);
    
    // ... 其他方法 ...
} 