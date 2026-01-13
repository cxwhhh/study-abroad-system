package com.study.service.adminuser;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员用户数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDTO {
    
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String name;
    private String email;
    private String phone;
    private String role;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private String lastLoginIp;
    private Date lastLoginTime;
    
    // 用于批量更新状态
    private Long[] adminIds;
} 