package com.study.service.log;

import java.util.Date;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * 操作日志记录处理
 */
@Aspect
@Component
@Slf4j
public class LogAspect {
    
    @Autowired
    private LogService logService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 配置切入点，匹配所有标记了LogOperation注解的方法
     */
    @Pointcut("@annotation(com.study.service.log.LogOperation)")
    public void logPointCut() {}
    
    /**
     * 环绕通知处理
     */
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        
        // 获取当前请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            // 非Web请求环境下直接执行方法
            return joinPoint.proceed();
        }
        
        HttpServletRequest request = attributes.getRequest();
        
        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LogOperation logOperation = signature.getMethod().getAnnotation(LogOperation.class);
        
        // 创建日志对象
        Log operLog = new Log();
        
        try {
            // 设置操作模块
            operLog.setTitle(logOperation.module());
            
            // 设置操作类型
            operLog.setOperType(logOperation.operType());
            
            // 设置操作描述信息
            operLog.setMethod(request.getMethod() + " " + request.getRequestURI());
            
            // 设置操作URL
            operLog.setOperUrl(request.getRequestURI());
            
            // 设置请求方式
            operLog.setMethod(request.getMethod());
            
            // 设置操作IP地址
            operLog.setOperIp(getIpAddress(request));
            
            // 设置操作地址
            operLog.setOperLocation(getLocationByIp(operLog.getOperIp()));
            
            // 设置请求参数
            if (logOperation.saveParams()) {
                setRequestParams(joinPoint, operLog);
            }
            
            // 设置操作时间
            operLog.setOperTime(new Date());
            
            // 获取当前用户信息（可根据实际情况自行实现）
            setCurrentUser(operLog, request);
            
            // 执行目标方法
            result = joinPoint.proceed();
            
            // 设置操作状态（0=正常）
            operLog.setStatus("0");
            
            // 设置返回结果
            if (logOperation.saveResult() && result != null) {
                operLog.setJsonResult(objectMapper.writeValueAsString(result));
            }
            
        } catch (Exception e) {
            // 记录异常信息
            operLog.setStatus("1"); // 状态为异常
            operLog.setErrorMsg(e.getMessage());
            log.error("操作出现异常:", e);
            throw e;
        } finally {
            // 记录执行时间
            long costTime = System.currentTimeMillis() - startTime;
            log.info("操作执行时间: {}ms", costTime);
            
            // 保存日志
            try {
                logService.saveLog(operLog);
            } catch (Exception e) {
                log.error("保存操作日志失败:", e);
            }
        }
        
        return result;
    }
    
    /**
     * 设置请求参数
     */
    private void setRequestParams(JoinPoint joinPoint, Log operLog) {
        try {
            String params = objectMapper.writeValueAsString(joinPoint.getArgs());
            operLog.setOperParam(params);
        } catch (Exception e) {
            log.error("设置请求参数失败:", e);
            operLog.setOperParam("获取请求参数失败");
        }
    }
    
    /**
     * 设置当前用户信息
     */
    private void setCurrentUser(Log operLog, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                operLog.setOperName("未登录用户");
                return;
            }

            Object userObj = session.getAttribute("user");
            if (userObj == null) {
                userObj = session.getAttribute("admin");
            }
            if (userObj == null) {
                userObj = session.getAttribute("adminInfo");
            }
            if (userObj != null) {
                Map<?, ?> user = objectMapper.convertValue(userObj, Map.class);
                Object id = user.get("id");
                if (id != null) {
                    operLog.setOperUserId(Long.valueOf(id.toString()));
                }
                Object username = user.get("username");
                if (username != null && !username.toString().isBlank()) {
                    operLog.setOperName(username.toString());
                    return;
                }
                Object nickname = user.get("nickname");
                if (nickname != null && !nickname.toString().isBlank()) {
                    operLog.setOperName(nickname.toString());
                    return;
                }
                operLog.setOperName("未知用户");
            } else {
                operLog.setOperName("未登录用户");
            }
        } catch (Exception e) {
            log.error("获取当前用户信息失败:", e);
            operLog.setOperName("未知用户");
        }
    }
    
    /**
     * 获取IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 对于通过多个代理的情况，第一个IP为客户端真实IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
    
    /**
     * 根据IP获取地理位置（简化实现）
     */
    private String getLocationByIp(String ip) {
        // 这里可以接入IP地址库或第三方服务实现IP地址到地理位置的转换
        // 简化起见，这里直接返回本地网络
        return "本地网络";
    }
} 
