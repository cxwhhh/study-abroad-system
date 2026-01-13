package com.study.service.admin;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import org.hibernate.annotations.Immutable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员/教师实体类 (只读)
 * 
 * 此实体类映射到admin表，但仅用于查询操作。
 * 由于系统中存在两个实体类(Admin和AdminUser)映射到同一个表，
 * 为了避免冲突，此实体使用@Immutable注解标记为只读。
 * 所有的创建、更新和删除操作应该通过AdminUser实体进行。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Immutable
@Table(name = "admin")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名
     */
    @Column(nullable = false, length = 50, unique = true)
    private String username;

    /**
     * 密码
     */
    @Column(nullable = false, length = 100)
    private String password;

    /**
     * 姓名
     */
    @Column(length = 50)
    private String name;

    /**
     * 邮箱
     */
    @Column(length = 100)
    private String email;

    /**
     * 角色
     */
    @Column(length = 20)
    private String role;

    /**
     * 用户状态 (1:活跃, 0:未激活, 2:已禁用)
     */
    @Column
    private Integer status;

    /**
     * 最后登录时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_login_time")
    private Date lastLogin;

    /**
     * 创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    /**
     * 更新时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;
} 