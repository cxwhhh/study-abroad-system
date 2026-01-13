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
 * 金斯顿大学 (Kingston University) 匹配算法
 * 
 * 规则来源：
 * 1. Prestigious Institution (985/211): 2:1 -> 70%, 2:2 -> 65%
 * 2. Any Institution (双非): 2:1 -> 75%, 2:2 -> 70%
 */
@Service
@SuppressWarnings("unused")
public class UKKingstonMatchingAlgorithm implements SchoolMatchingStrategy {

    private static final Logger log = LoggerFactory.getLogger(UKKingstonMatchingAlgorithm.class);

    @Autowired
    private MatchingDataService dataService;

    @Autowired
    private ObjectMapper objectMapper;

    // 定义 "Prestigious" (知名) 院校名单 (主要是 985/211)
    private static final Set<String> PRESTIGIOUS_UNIVERSITIES = new HashSet<>();

    static {
        // 985 Universities
        String[] list985 = {
                "清华大学", "北京大学", "厦门大学", "中国科学技术大学", "南京大学", "复旦大学", "天津大学", "哈尔滨工业大学",
                "浙江大学", "西安交通大学", "华中科技大学", "东南大学", "武汉大学", "上海交通大学", "山东大学", "湖南大学",
                "中国人民大学", "北京理工大学", "吉林大学", "重庆大学", "电子科技大学", "四川大学", "中山大学", "华南理工大学",
                "兰州大学", "西北工业大学", "东北大学", "北京航空航天大学", "华东师范大学", "中国农业大学", "中国海洋大学",
                "中央民族大学", "西北农林科技大学", "同济大学", "北京师范大学", "南开大学", "中南大学", "大连理工大学"
        };

        // 211 Universities (部分)
        String[] list211 = {
                "北京交通大学", "北京工业大学", "北京科技大学", "北京化工大学", "北京邮电大学", "中国传媒大学",
                "中央音乐学院", "对外经济贸易大学", "北京中医药大学", "北京外国语大学", "中国政法大学",
                "中国石油大学", "中央财经大学", "北京体育大学", "中国矿业大学", "中国地质大学",
                "上海大学", "东华大学", "上海财经大学", "上海戏剧学院", "华东理工大学",
                "南京师范大学", "苏州大学", "江南大学", "河海大学", "中国药科大学", "南京航空航天大学", "南京理工大学",
                "安徽大学", "合肥工业大学", "福州大学", "南昌大学", "郑州大学", "武汉理工大学", "华中师范大学",
                "湖南师范大学", "暨南大学", "华南师范大学", "西南大学", "西南财经大学", "西南交通大学",
                "长安大学", "西北大学", "西安电子科技大学"
                // ... 实际项目中可以导入完整的211名单
        };

        Collections.addAll(PRESTIGIOUS_UNIVERSITIES, list985);
        Collections.addAll(PRESTIGIOUS_UNIVERSITIES, list211);
    }

    @Override
    public String getSchoolName() {
        return "金斯顿大学";
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
            List<School> ukSchools = dataService.getSchoolsByRegion("英国");
            school = ukSchools.stream()
                    .filter(s -> s.getEnglishName() != null
                            && s.getEnglishName().equalsIgnoreCase("Kingston University"))
                    .findFirst()
                    .orElse(null);

            if (school == null) {
                log.warn("未在数据库中找到金斯顿大学");
                return results;
            }
        }

        // 2. 获取学生背景
        MatchingRequest.StudentInfoDTO studentInfo = request.getStudentInfo();
        String undergradSchool = studentInfo.getUndergraduateSchool();

        // 判断是否为 "Prestigious" 院校
        boolean isPrestigious = isPrestigiousUniversity(undergradSchool);

        // 3. 遍历专业
        List<Major> majors = dataService.getMajorsBySchoolId(school.getId());
        List<String> majorTokens = UKSingleSchoolMatchingAlgorithmBase.extractMajorTokens(request);
        majors = UKSingleSchoolMatchingAlgorithmBase.selectMajorsByTokens(majors, majorTokens, 10);

        for (Major major : majors) {
            // 3.1 判断该专业要求的英位等级 (2:1 还是 2:2)
            // 金斯顿大学大部分专业接受 2:2，部分热门可能要求 2:1
            String ukDegreeReq = determineUKDegreeRequirement(major.getName());

            // 3.2 获取分数线
            double requiredScore = getRequiredScore(isPrestigious, ukDegreeReq);

            // 3.3 计算匹配度
            double matchScore = calculateMatchScore(studentInfo, requiredScore, school, major.getName());
            String matchLevel = determineMatchLevel(matchScore);
            String matchReason = generateMatchReason(studentInfo, isPrestigious, requiredScore, ukDegreeReq);

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
            result.setAlgorithmStrategy("UK_KINGSTON_MATCHING_ALGORITHM");

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
     * 判断是否为 Prestigious 院校
     */
    private boolean isPrestigiousUniversity(String schoolName) {
        if (schoolName == null)
            return false;
        // 简单模糊匹配
        return PRESTIGIOUS_UNIVERSITIES.stream().anyMatch(schoolName::contains);
    }

    /**
     * 确定专业要求的学位等级
     * 金斯顿大学：
     * - 大多数课程要求 2:2
     * - 某些特定课程（如心理学、某些设计类）可能要求 2:1
     * - 这里默认设为 2:2 以覆盖大多数情况，特殊关键词设为 2:1
     */
    private String determineUKDegreeRequirement(String majorName) {
        if (majorName == null)
            return "2:2";
        String name = majorName.toLowerCase();

        // 假设心理学等专业要求较高
        if (name.contains("psychology") || name.contains("clinical")) {
            return "2:1";
        }

        // 默认为 2:2 (Good Honours Degree)
        return "2:2";
    }

    /**
     * 获取要求的均分 (百分制)
     * 对应图片中的逻辑
     */
    private double getRequiredScore(boolean isPrestigious, String ukDegree) {
        if (isPrestigious) {
            // "Prestigious" institution on ECCTIS
            if ("2:1".equals(ukDegree))
                return 70.0;
            else
                return 65.0;
        } else {
            // Any institution on ECCTIS
            if ("2:1".equals(ukDegree))
                return 75.0;
            else
                return 70.0;
        }
    }

    private double getStudentAverageScore(MatchingRequest.StudentInfoDTO student) {
        if (student.getGpa() == null)
            return 0.0;
        return convertGpaToPercentage(student.getGpa(), student.getGpaScale());
    }

    private double convertGpaToPercentage(Double gpa, String scaleRaw) {
        if (gpa == null) {
            return 0.0;
        }
        if (scaleRaw == null || scaleRaw.trim().isEmpty() || "0".equals(scaleRaw.trim())) {
            return clamp(gpa, 0.0, 100.0);
        }
        String scale = scaleRaw.trim();
        if ("100".equals(scale)) {
            return clamp(gpa, 0.0, 100.0);
        }
        Double s;
        try {
            s = Double.parseDouble(scale);
        } catch (NumberFormatException e) {
            return clamp(gpa, 0.0, 100.0);
        }
        if (s <= 0.0) {
            return clamp(gpa, 0.0, 100.0);
        }
        return clamp((gpa / s) * 100.0, 0.0, 100.0);
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private double calculateMatchScore(MatchingRequest.StudentInfoDTO student, double required, School school,
            String majorName) {
        double avgScore = getStudentAverageScore(student);

        if (avgScore == 0.0)
            return 0.0;

        double baseScore = 0.0;

        // 1. 均分评分 (权重 80%) - 金斯顿对均分要求比较刚性
        if (avgScore >= required) {
            baseScore += 80.0; // 达标
            if (avgScore >= required + 2)
                baseScore += 5; // 高分加成
            if (avgScore >= required + 5)
                baseScore += 5; // 极高分
        } else if (avgScore >= required - 2) {
            // 稍微差一点 (Argue区间)
            baseScore += 60.0 + (avgScore - (required - 2)) * 10.0;
        } else {
            // 差距较大
            baseScore += 30.0;
        }

        // 2. 专业相关度 (权重 10%)
        String target = student.getTargetMajor() != null ? student.getTargetMajor() : "";
        if (majorName.contains(target) || target.isEmpty()) {
            baseScore += 10.0;
        }

        return Math.min(100.0, baseScore);
    }

    private String determineMatchLevel(double score) {
        if (score >= 90)
            return "保底";
        if (score >= 80)
            return "稳妥";
        if (score >= 60)
            return "冲刺";
        return "不建议";
    }

    private String generateMatchReason(MatchingRequest.StudentInfoDTO student, boolean isPrestigious, double required,
            String ukDegree) {
        double avgScore = getStudentAverageScore(student);

        StringBuilder sb = new StringBuilder();
        sb.append("【金斯顿大学】");

        if (isPrestigious) {
            sb.append("您的院校属于 Prestigious 知名院校（985/211/双一流）。");
        } else {
            sb.append("您的院校属于普通本科院校（Any Institution）。");
        }

        sb.append("该专业要求英国 ").append(ukDegree).append(" 学位，对应均分要求为 ").append((int) required).append("%。");
        sb.append(" 您的均分为 ").append(String.format("%.1f", avgScore)).append("%。");

        if (avgScore >= required) {
            sb.append(" 成绩完全达标，录取希望很大。");
        } else if (avgScore >= required - 2) {
            sb.append(" 成绩略有差距，建议在文书中突出实践经验或作品集（如适用）。");
        } else {
            sb.append(" 成绩未达标，建议提升均分或考虑预科课程。");
        }

        return sb.toString();
    }

    private double calculateAdmissionProbability(double score) {
        if (score >= 90)
            return 0.98;
        if (score >= 80)
            return 0.90;
        if (score >= 60)
            return 0.50;
        return 0.15;
    }

    private double calculateRecommendationIndex(double score) {
        return Math.min(1.0, score / 100.0);
    }
}
