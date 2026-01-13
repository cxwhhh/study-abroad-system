package com.study.service.news;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 新闻资讯实体类
 * 用于存储留学相关的新闻和资讯信息
 */
@Data
public class News {
    /** 新闻ID */
    private Long id;
    /** 新闻标题 */
    private String title;
    /** 新闻摘要 */
    private String summary;
    /** 新闻内容 */
    private String content;
    /** 新闻配图URL */
    private String imageUrl;
    /** 新闻分类 */
    private String category;
    /** 浏览次数 */
    private Integer views;
    
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
} 