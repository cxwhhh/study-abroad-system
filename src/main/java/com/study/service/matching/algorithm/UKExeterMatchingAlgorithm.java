package com.study.service.matching.algorithm;

import com.study.service.matching.MatchingDataService;
import com.study.service.matching.MatchingResult;
import com.study.service.matching.dto.MatchingRequest;
import com.study.service.schools.School;
import com.study.service.major.Major;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 埃克塞特大学 (University of Exeter) 匹配算法
 * 
 * 规则来源：
 * 1. 英国 2:1 学位要求 -> 均分 75% (灵活降分区间 3分 -> 最低 72%)
 * 2. 英国 2:2 学位要求 -> 均分 70% (灵活降分区间 2分 -> 最低 68%)
 */
@Service
@SuppressWarnings("unused")
public class UKExeterMatchingAlgorithm implements SchoolMatchingStrategy {

    private static final Logger log = LoggerFactory.getLogger(UKExeterMatchingAlgorithm.class);

    @Autowired
    private MatchingDataService dataService;

    @Autowired
    private ObjectMapper objectMapper;

    // 埃克塞特大学通常参考软科/网大排名，这里定义一个认可院校名单集合 (Band A/Tier 1)
    // 只有在名单内的学校才享受 75%/70% 的政策，双非或排名靠后通常要求 80% 或 85%
    // 这里复用之前的高校名单作为"认可名单"
    private static final Set<String> RECOGNIZED_UNIVERSITIES = new HashSet<>();
    static {
        // 985/211 及部分一本双非 (模拟埃克塞特大学的 Tier 1 list)
        RECOGNIZED_UNIVERSITIES.addAll(Arrays.asList(
                "清华大学", "北京大学", "浙江大学", "上海交通大学", "复旦大学", "南京大学", "中国科学技术大学",
                "武汉大学", "华中科技大学", "西安交通大学", "哈尔滨工业大学", "中山大学", "北京师范大学",
                "四川大学", "山东大学", "同济大学", "南开大学", "天津大学", "中南大学", "吉林大学",
                "东南大学", "厦门大学", "北京航空航天大学", "北京理工大学", "大连理工大学", "华南理工大学",
                "中国农业大学", "湖南大学", "华东师范大学", "兰州大学", "西北工业大学", "电子科技大学",
                "重庆大学", "东北大学", "中国海洋大学", "中国人民大学", "北京交通大学", "北京科技大学",
                "南京理工大学", "南京航空航天大学", "哈尔滨工程大学", "上海大学", "苏州大学", "暨南大学",
                "深圳大学", "南方科技大学", "上海财经大学", "中央财经大学", "对外经济贸易大学"
        // ... 实际项目中可继续补充完整名单
        ));
    }

    @Override
    public String getSchoolName() {
        return "埃克塞特大学";
    }

    @Override
    public String getRegion() {
        return "英国";
    }

    @Override
    public List<MatchingResult> computeMatchingResults(MatchingRequest request) {
        List<MatchingResult> results = new ArrayList<>();

        // 1. 查找学校实体
        School school = dataService.getSchoolByName(getSchoolName());
        if (school == null) {
            // 尝试英文名
            List<School> ukSchools = dataService.getSchoolsByRegion("英国");
            school = ukSchools.stream()
                    .filter(s -> s.getEnglishName() != null
                            && s.getEnglishName().equalsIgnoreCase("University of Exeter"))
                    .findFirst()
                    .orElse(null);

            if (school == null) {
                log.warn("未在数据库中找到埃克塞特大学");
                return results;
            }
        }

        // 2. 获取学生背景
        MatchingRequest.StudentInfoDTO studentInfo = request.getStudentInfo();
        String undergradSchool = studentInfo.getUndergraduateSchool();

        // 判断学生学校是否在认可名单内
        // 如果在名单内，使用图片上的 75/70 标准
        // 如果不在，通常要求更高 (例如 +5分)，这里设为 Tier 2 标准
        boolean isRecognizedUni = isRecognizedUniversity(undergradSchool);

        // 3. 遍历专业
        List<Major> majors = dataService.getMajorsBySchoolId(school.getId());
        List<String> majorTokens = UKSingleSchoolMatchingAlgorithmBase.extractMajorTokens(request);
        majors = UKSingleSchoolMatchingAlgorithmBase.selectMajorsByTokens(majors, majorTokens, 10);

        for (Major major : majors) {
            // 3.1 判断该专业要求的英位等级 (2:1 还是 2:2)
            // 这一步通常需要数据库字段支持，如果没有，我们根据专业类型进行推断
            String ukDegreeReq = determineUKDegreeRequirement(major.getName());

            // 3.2 获取分数线和灵活降分区间
            double requiredGpa;
            double flexibleDrop;

            if (isRecognizedUni) {
                // 名单内院校：执行图片上的标准
                if ("2:1".equals(ukDegreeReq)) {
                    requiredGpa = 75.0;
                    flexibleDrop = 3.0;
                } else {
                    requiredGpa = 70.0;
                    flexibleDrop = 2.0;
                }
            } else {
                // 名单外院校：标准提高 5 分，灵活区间保持不变或缩小
                if ("2:1".equals(ukDegreeReq)) {
                    requiredGpa = 80.0;
                    flexibleDrop = 3.0;
                } else {
                    requiredGpa = 75.0;
                    flexibleDrop = 2.0;
                }
            }

            // 3.3 计算匹配度
            double matchScore = calculateMatchScore(studentInfo, requiredGpa, flexibleDrop, school, major.getName());
            String matchLevel = determineMatchLevel(matchScore);
            String matchReason = generateMatchReason(studentInfo, requiredGpa, flexibleDrop, isRecognizedUni,
                    ukDegreeReq);

            // 3.4 构建结果
            MatchingResult result = new MatchingResult();
            result.setUserId(studentInfo.getUserId());
            result.setSchoolId(school.getId());
            result.setSchoolName(school.getName());
            result.setMajorName(major.getName());
            result.setMatchScore(matchScore);
            result.setMatchLevel(matchLevel);
            result.setMatchReason(matchReason);
            result.setAdmissionProbability(calculateAdmissionProbability(matchScore));
            result.setRecommendationIndex(calculateRecommendationIndex(matchScore));
            result.setRegion("英国");
            result.setAlgorithmStrategy("UK_EXETER_MATCHING_ALGORITHM");

            try {
                result.setStudentInfoSnapshot(objectMapper.writeValueAsString(studentInfo));
            } catch (Exception e) {
                result.setStudentInfoSnapshot("{}");
            }

            results.add(result);
        }

        // 排序
        results.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));
        return results;
    }

    private static List<Major> filterMajorsByTokens(List<Major> majors, List<String> tokens) {
        if (majors == null || majors.isEmpty() || tokens == null || tokens.isEmpty()) {
            return Collections.emptyList();
        }
        List<Major> out = new ArrayList<>();
        for (Major m : majors) {
            if (m == null) {
                continue;
            }
            String cn = m.getName() == null ? "" : m.getName().trim().toLowerCase(Locale.ROOT);
            String en = m.getEnglishName() == null ? "" : m.getEnglishName().trim().toLowerCase(Locale.ROOT);
            for (String t : tokens) {
                if ((!cn.isEmpty() && (cn.equals(t) || cn.contains(t) || t.contains(cn)))
                        || (!en.isEmpty() && (en.equals(t) || en.contains(t) || t.contains(en)))) {
                    out.add(m);
                    break;
                }
            }
        }
        return out;
    }

    private static List<String> extractMajorTokens(MatchingRequest request) {
        if (request == null) {
            return Collections.emptyList();
        }
        Set<String> tokens = new LinkedHashSet<>();
        if (request.getTargetMajors() != null && !request.getTargetMajors().isEmpty()) {
            for (String t : request.getTargetMajors()) {
                tokens.addAll(splitTokens(t));
            }
        }
        String keyword = request.getStudentInfo() == null ? null : request.getStudentInfo().getTargetMajor();
        tokens.addAll(splitTokens(keyword));
        return new ArrayList<>(tokens);
    }

    private static List<String> splitTokens(String raw) {
        if (raw == null) {
            return Collections.emptyList();
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return Collections.emptyList();
        }
        String[] parts = trimmed.split("[,，;；、\\s]+");
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            if (p == null) {
                continue;
            }
            String v = p.trim().toLowerCase(Locale.ROOT);
            if (!v.isEmpty()) {
                out.add(v);
            }
        }
        return out;
    }

    /**
     * 判断是否为 2:1 还是 2:2 专业
     * 如果数据库没有字段，则通过名称简单推断
     * 商科、计算机、工程通常是 2:1 (75%)
     * 部分社科、教育可能是 2:2 (70%)
     */
    private String determineUKDegreeRequirement(String majorName) {
        if (majorName == null)
            return "2:1";

        // 假设大部分热门专业都是 2:1
        // 只有特定少部分可能是 2:2，这里为了保险起见，默认大部分为 2:1
        // 如果有明确的 2:2 专业列表可以添加判断
        // 例如：TESOL, Education 在某些学校可能是 2:2，但在 Exeter 商学院全是 2:1

        return "2:1";
    }

    private boolean isRecognizedUniversity(String schoolName) {
        if (schoolName == null)
            return false;
        return RECOGNIZED_UNIVERSITIES.stream().anyMatch(schoolName::contains);
    }

    /**
     * 计算匹配分数
     * 逻辑：
     * - 超过要求：分数很高
     * - 在要求和 (要求-灵活分) 之间：分数中等 (Argue区间)
     * - 低于 (要求-灵活分)：分数低
     */
    private double calculateMatchScore(MatchingRequest.StudentInfoDTO student, double required, double flexDrop,
            School school, String majorName) {
        if (student.getGpa() == null)
            return 0.0;

        double gpa = convertGpaToPercentage(student.getGpa(), student.getGpaScale());
        double minAcceptable = required - flexDrop; // 最低可接受分数 (例如 75-3=72)

        double baseScore = 0.0;

        // 1. GPA 评分 (权重 60%)
        if (gpa >= required) {
            baseScore += 60.0; // 达标
            if (gpa >= required + 5)
                baseScore += 5; // 高分加成
        } else if (gpa >= minAcceptable) {
            // 在灵活区间内 (例如 72-74.9)
            // 分数在 40-55 之间浮动
            baseScore += 40.0 + ((gpa - minAcceptable) / flexDrop) * 15.0;
        } else {
            // 低于最低要求
            baseScore += 10.0;
        }

        // 2. 学校背景评分 (权重 20%)
        // Exeter 也是罗素集团
        baseScore += 15.0;

        // 3. 专业相关度 (权重 20%)
        // 简单判断
        String target = student.getTargetMajor() != null ? student.getTargetMajor() : "";
        if (majorName.contains(target) || target.isEmpty()) {
            baseScore += 20.0;
        } else {
            baseScore += 10.0;
        }

        return Math.min(100.0, baseScore);
    }

    private double convertGpaToPercentage(Double gpa, String scale) {
        if (gpa == null)
            return 0.0;
        if ("100".equals(scale))
            return gpa;
        if ("4".equals(scale) || "4.0".equals(scale))
            return (gpa / 4.0) * 100.0;
        if ("5".equals(scale) || "5.0".equals(scale))
            return (gpa / 5.0) * 100.0;
        return gpa;
    }

    private String determineMatchLevel(double score) {
        if (score >= 90)
            return "保底";
        if (score >= 80)
            return "稳妥";
        if (score >= 60)
            return "冲刺"; // 在灵活区间内通常属于冲刺
        return "不建议";
    }

    private String generateMatchReason(MatchingRequest.StudentInfoDTO student, double required, double flex,
            boolean isRecognized, String ukDegree) {
        double gpa = convertGpaToPercentage(student.getGpa(), student.getGpaScale());
        double minAcceptable = required - flex;

        StringBuilder sb = new StringBuilder();
        sb.append("【埃克塞特大学】");

        if (isRecognized) {
            sb.append("您的院校在认可名单内。");
        } else {
            sb.append("您的院校属于非核心名单，分数要求相应提高。");
        }

        sb.append("该专业要求英国 ").append(ukDegree).append(" 学位，对应均分 ").append((int) required).append("%。");

        if (flex > 0) {
            sb.append(" 根据政策，存在 ").append((int) flex).append(" 分的灵活降分空间（最低 ").append((int) minAcceptable).append("%）。");
        }

        sb.append(" 您的均分为 ").append(String.format("%.1f", gpa)).append("%。");

        if (gpa >= required) {
            sb.append(" 成绩完全达标，录取希望很大。");
        } else if (gpa >= minAcceptable) {
            sb.append(" 成绩处于灵活录取区间，建议在文书中突出其他优势进行争取。");
        } else {
            sb.append(" 成绩低于最低灵活要求，申请风险较高。");
        }

        return sb.toString();
    }

    private double calculateAdmissionProbability(double score) {
        if (score >= 90)
            return 0.95;
        if (score >= 80)
            return 0.85;
        if (score >= 60)
            return 0.45; // 灵活区间内概率减半
        return 0.10;
    }

    private double calculateRecommendationIndex(double score) {
        return Math.min(1.0, score / 100.0);
    }
}
