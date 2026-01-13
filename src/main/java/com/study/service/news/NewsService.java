package com.study.service.news;

import java.util.List;

/**
 * 新闻资讯服务接口
 * 处理新闻资讯相关的业务逻辑
 */
public interface NewsService {
    /**
     * 获取新闻列表
     *
     * @return 新闻列表
     */
    List<News> getNewsList();

    /**
     * 根据ID获取新闻详情
     *
     * @param id 新闻ID
     * @return 新闻信息
     */
    News getNewsById(Long id);

    /**
     * 创建新闻
     *
     * @param news 新闻信息
     * @return 影响的行数
     */
    int createNews(News news);

    /**
     * 更新新闻
     *
     * @param news 新闻信息
     * @return 影响的行数
     */
    int updateNews(News news);

    /**
     * 删除新闻
     *
     * @param id 新闻ID
     * @return 影响的行数
     */
    int deleteNews(Long id);
} 