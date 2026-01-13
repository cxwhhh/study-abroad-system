package com.study.service.news;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 新闻资讯数据访问接口
 */
@Mapper
public interface NewsMapper {
    /**
     * 查询所有新闻列表
     *
     * @return 新闻列表
     */
    List<News> selectList();

    /**
     * 根据ID查询新闻
     *
     * @param id 新闻ID
     * @return 新闻信息
     */
    News selectById(Long id);

    /**
     * 插入新闻
     *
     * @param news 新闻信息
     * @return 影响的行数
     */
    int insert(News news);

    /**
     * 更新新闻
     *
     * @param news 新闻信息
     * @return 影响的行数
     */
    int updateById(News news);

    /**
     * 删除新闻
     *
     * @param id 新闻ID
     * @return 影响的行数
     */
    int deleteById(Long id);
} 