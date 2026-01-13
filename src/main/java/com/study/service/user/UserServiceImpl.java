package com.study.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
// 替换为 Jakarta EE 导入
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public Long getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            try {
                // 尝试从session获取userId属性
                Object userId = session.getAttribute("userId");
                if (userId != null) {
                    log.info("从session中获取到userId: {}", userId);
                    if (userId instanceof Number) {
                        return ((Number) userId).longValue();
                    } else if (userId instanceof String) {
                        return Long.parseLong((String) userId);
                    }
                }
                
                // 尝试从user对象中获取ID
                User user = (User) session.getAttribute("user");
                if (user != null && user.getId() != null) {
                    log.info("从session中的user对象获取到userId: {}", user.getId());
                    return user.getId();
                }
                
                // 记录所有可用的session属性以便调试
                log.info("无法获取用户ID，记录所有session属性");
                java.util.Enumeration<String> attrNames = session.getAttributeNames();
                while (attrNames.hasMoreElements()) {
                    String name = attrNames.nextElement();
                    log.info("Session属性: {} = {}", name, session.getAttribute(name));
                }
            } catch (Exception e) {
                log.error("从session获取用户ID时出错", e);
            }
        } else {
            log.warn("用户session为null");
        }
        return null;
    }
    
    @Override
    public User getUserById(Long userId) {
        if (userId == null) {
            log.warn("用户ID为空，不获取用户信息");
            return null;
        }
        
        try {
            log.info("获取用户ID为 {} 的用户信息", userId);
            User user = userMapper.findById(userId);
            if (user == null) {
                log.info("未找到用户ID为 {} 的用户信息", userId);
            } else {
                // 清除敏感信息
                user.setPassword(null);
            }
            return user;
        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取用户信息失败", e);
        }
    }
    
    @Override
    public User getCurrentUser(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                log.warn("用户session为null");
                return null;
            }
            
            // 先尝试直接从session获取user对象
            User user = (User) session.getAttribute("user");
            if (user != null) {
                // 确保没有敏感信息
                user.setPassword(null);
                return user;
            }
            
            // 如果session中没有直接存储user对象，尝试获取userId然后查询数据库
            Long userId = getCurrentUserId(request);
            if (userId != null) {
                return getUserById(userId);
            }
            
            log.warn("无法获取当前用户信息");
            return null;
        } catch (Exception e) {
            log.error("获取当前用户信息失败: {}", e.getMessage(), e);
            return null;
        }
    }
} 
