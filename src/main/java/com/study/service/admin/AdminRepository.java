package com.study.service.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Admin实体的只读仓库
 * 该仓库只用于查询操作，不提供修改数据的方法
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    
    /**
     * 根据用户名查找管理员
     * @param username 用户名
     * @return 管理员信息
     */
    Admin findByUsername(String username);
    
    /**
     * 根据用户名和密码查找管理员（用于登录）
     * @param username 用户名
     * @param password 密码
     * @return 管理员信息
     */
    @Query("SELECT a FROM Admin a WHERE a.username = :username AND a.password = :password")
    Admin findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
} 