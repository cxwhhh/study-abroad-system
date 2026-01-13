package com.study.service.log;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

/**
 * IP地址工具类
 */
public class IpUtils {
    
    private static final String UNKNOWN = "unknown";
    
    /**
     * 获取客户端IP地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "127.0.0.1";
        }
        
        String ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.hasText(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (!StringUtils.hasText(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (!StringUtils.hasText(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (!StringUtils.hasText(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (!StringUtils.hasText(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 多级代理时，第一个IP为客户端真实IP
        if (StringUtils.hasText(ip) && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
    
    /**
     * 判断是否是内网IP
     */
    public static boolean isInternalIp(String ip) {
        if (StringUtils.hasText(ip) && !"127.0.0.1".equals(ip)) {
            byte[] addr = textToNumericFormatV4(ip);
            if (addr != null) {
                return isInternalIp(addr);
            }
        }
        return true;
    }
    
    /**
     * 根据IP地址判断是否是内网IP
     */
    private static boolean isInternalIp(byte[] addr) {
        if (addr == null || addr.length < 2) {
            return true;
        }
        
        // 内网IP: 10.x.x.x
        final byte b0 = addr[0];
        if (b0 == 10) {
            return true;
        }
        
        // 内网IP: 172.16.x.x - 172.31.x.x
        final byte b1 = addr[1];
        if (b0 == (byte) 172 && (b1 >= 16 && b1 <= 31)) {
            return true;
        }
        
        // 内网IP: 192.168.x.x
        if (b0 == (byte) 192 && b1 == (byte) 168) {
            return true;
        }
        
        // 局域网IP: 169.254.x.x
        if (b0 == (byte) 169 && b1 == (byte) 254) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 将IPv4地址文本转换为字节数组
     */
    private static byte[] textToNumericFormatV4(String text) {
        if (!StringUtils.hasText(text)) {
            return null;
        }
        
        byte[] bytes = new byte[4];
        String[] elements = text.split("\\.", -1);
        if (elements.length != 4) {
            return null;
        }
        
        try {
            for (int i = 0; i < elements.length; i++) {
                int value = Integer.parseInt(elements[i]);
                if (value < 0 || value > 255) {
                    return null;
                }
                bytes[i] = (byte) value;
            }
        } catch (NumberFormatException e) {
            return null;
        }
        
        return bytes;
    }
} 