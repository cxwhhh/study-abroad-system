package com.study.service.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public AdminDTO login(String username, String password) throws Exception {
        // 使用Repository进行查询
        Admin admin = adminRepository.findByUsernameAndPassword(username, password);
        
        if (admin == null) {
            throw new Exception("用户名或密码错误");
        }
        
        // 转换为DTO
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setId(admin.getId().intValue());
        adminDTO.setUsername(admin.getUsername());
        adminDTO.setNickname(admin.getName()); // 使用name字段填充nickname
        adminDTO.setRole(admin.getRole() != null ? admin.getRole() : "管理员"); // 设置角色
        
        return adminDTO;
    }
} 