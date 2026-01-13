package com.study.service.matching;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 匹配结果数据访问层
 */
@Repository
public interface MatchingResultRepository extends JpaRepository<MatchingResult, Long> {

    /**
     * 根据用户ID查询匹配结果
     */
    List<MatchingResult> findByUserIdOrderByMatchScoreDesc(Long userId);

    /**
     * 根据用户ID分页查询匹配结果
     */
    Page<MatchingResult> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据用户ID和地区查询匹配结果
     */
    List<MatchingResult> findByUserIdAndRegionOrderByMatchScoreDesc(Long userId, String region);

    /**
     * 根据用户ID和学校ID查询匹配结果
     */
    List<MatchingResult> findByUserIdAndSchoolIdOrderByMatchScoreDesc(Long userId, Long schoolId);

    /**
     * 根据用户ID查找收藏的匹配结果
     * @param userId 用户ID
     * @return 收藏的匹配结果列表
     */
    List<MatchingResult> findByUserIdAndIsFavoriteTrueOrderByMatchScoreDesc(Long userId);

    /**
     * 根据用户ID和匹配等级查询匹配结果
     */
    List<MatchingResult> findByUserIdAndMatchLevelOrderByMatchScoreDesc(Long userId, String matchLevel);

    /**
     * 根据用户ID和分数范围查询匹配结果
     */
    @Query("SELECT mr FROM MatchingResult mr WHERE mr.userId = :userId AND mr.matchScore BETWEEN :minScore AND :maxScore ORDER BY mr.matchScore DESC")
    List<MatchingResult> findByUserIdAndScoreRange(@Param("userId") Long userId, 
                                                  @Param("minScore") Double minScore, 
                                                  @Param("maxScore") Double maxScore);

    /**
     * 根据用户ID和分数范围查询匹配结果（使用方法名查询）
     */
    List<MatchingResult> findByUserIdAndMatchScoreBetweenOrderByMatchScoreDesc(Long userId, Double minScore, Double maxScore);

    /**
     * 根据用户ID和收藏状态查询匹配结果
     */
    List<MatchingResult> findByUserIdAndIsFavoriteOrderByMatchScoreDesc(Long userId, Boolean isFavorite);

    /**
     * 删除用户的所有匹配结果
     */
    void deleteByUserId(Long userId);

    /**
     * 删除指定时间之前的匹配结果
     * @param dateTime 时间点
     */
    void deleteByCreatedAtBefore(LocalDateTime dateTime);

    /**
     * 删除用户指定地区的匹配结果
     */
    void deleteByUserIdAndRegion(Long userId, String region);

    /**
     * 统计用户的匹配结果数量
     */
    long countByUserId(Long userId);

    /**
     * 根据用户ID和地区统计匹配结果数量
     */
    long countByUserIdAndRegion(Long userId, String region);

    /**
     * 根据用户ID和匹配等级统计匹配结果数量
     */
    long countByUserIdAndMatchLevel(Long userId, String matchLevel);

    /**
     * 根据用户ID和收藏状态统计匹配结果数量
     */
    long countByUserIdAndIsFavorite(Long userId, Boolean isFavorite);

    /**
     * 获取用户最新的匹配结果
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 最新的匹配结果列表
     */
    @Query("SELECT mr FROM MatchingResult mr WHERE mr.userId = :userId ORDER BY mr.createdAt DESC")
    List<MatchingResult> findLatestByUserId(@Param("userId") Long userId);
}