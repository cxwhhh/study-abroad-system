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
 * 诺丁汉大学 (University of Nottingham) 匹配算法
 * 
 * 规则来源：用户提供的官方入学要求截图
 * 
 * 1. 院校分级 (Tiering):
 * - Tier 1: 985/211/双一流院校
 * - Tier 2: 双非一本/二本 (Non-211 Public)
 * - Tier 3: 独立学院/民办 (Independent Colleges)
 * 
 * 2. 均分要求 (GPA - 严格对应图片):
 * - High 2:1 / 1st: Tier 1=75%, Tier 2=78%, Tier 3=82%
 * - 2:1 : Tier 1=72%, Tier 2=75%, Tier 3=80%
 * - 2:2 : Tier 1=68%, Tier 2=72%, Tier 3=75%
 */
@Service
@SuppressWarnings("unused")
public class UKNottinghamMatchingAlgorithm implements SchoolMatchingStrategy {

    private static final Logger log = LoggerFactory.getLogger(UKNottinghamMatchingAlgorithm.class);

    @Autowired
    private MatchingDataService dataService;

    @Autowired
    private ObjectMapper objectMapper;

    // =================================================================================
    // Tier 1: 985 / 211 / 双一流 (Core List)
    // =================================================================================
    private static final Set<String> TIER_1_UNIVERSITIES = new HashSet<>();
    static {
        // 985 & 211 核心名单
        String[] list = {
                "清华大学", "北京大学", "浙江大学", "上海交通大学", "复旦大学", "南京大学", "中国科学技术大学",
                "武汉大学", "华中科技大学", "西安交通大学", "哈尔滨工业大学", "中山大学", "北京师范大学",
                "四川大学", "山东大学", "同济大学", "南开大学", "天津大学", "中南大学", "吉林大学",
                "东南大学", "厦门大学", "北京航空航天大学", "北京理工大学", "大连理工大学", "华南理工大学",
                "中国农业大学", "湖南大学", "华东师范大学", "兰州大学", "西北工业大学", "电子科技大学",
                "重庆大学", "东北大学", "中国海洋大学", "中国人民大学", "北京交通大学", "北京科技大学",
                "南京理工大学", "南京航空航天大学", "哈尔滨工程大学", "上海大学", "苏州大学", "暨南大学",
                "深圳大学", "南方科技大学", "上海财经大学", "中央财经大学", "对外经济贸易大学",
                "中国政法大学", "北京邮电大学", "北京外国语大学", "上海外国语大学", "华东理工大学",
                "武汉理工大学", "西南交通大学", "河海大学", "南京师范大学", "郑州大学", "云南大学", "新疆大学",
                "西北大学", "福州大学", "南昌大学", "江南大学", "中国传媒大学"
        };
        Collections.addAll(TIER_1_UNIVERSITIES, list);
    }

    // Tier 2 通常指双非公办，代码逻辑中如果不是Tier 1且不是Tier 3(独立学院)，则默认为Tier 2

    // Tier 3 独立学院特征词 (用于模糊匹配)
    private static final String[] INDEPENDENT_COLLEGE_KEYWORDS = { "学院", "分校" };

    @Override
    public String getSchoolName() {
        return "诺丁汉大学";
    }

    @Override
    public String getRegion() {
        return "英国";
    }

    @Override
    public List<MatchingResult> computeMatchingResults(MatchingRequest request) {
        List<MatchingResult> results = new ArrayList<>();

        // 1. 查找学校
        School school = dataService.getSchoolByName(getSchoolName());
        if (school == null) {
            List<School> ukSchools = dataService.getSchoolsByRegion("英国");
            school = ukSchools.stream()
                    .filter(s -> s.getEnglishName() != null
                            && s.getEnglishName().equalsIgnoreCase("University of Nottingham"))
                    .findFirst()
                    .orElse(null);
            if (school == null) {
                log.warn("未在数据库中找到诺丁汉大学");
                return results;
            }
        }

        // 2. 获取学生背景
        MatchingRequest.StudentInfoDTO studentInfo = request.getStudentInfo();
        String undergradSchool = studentInfo.getUndergraduateSchool();

        // 判断学生院校层级 (Tier 1 / Tier 2 / Tier 3)
        String studentTier = getUniversityTier(undergradSchool);

        // 3. 遍历专业
        List<Major> majors = dataService.getMajorsBySchoolId(school.getId());
        List<String> majorTokens = UKSingleSchoolMatchingAlgorithmBase.extractMajorTokens(request);
        majors = UKSingleSchoolMatchingAlgorithmBase.selectMajorsByTokens(majors, majorTokens, 10);

        for (Major major : majors) {
            // 3.1 判断专业要求的学位等级 (High 2:1 / 2:1 / 2:2)
            String degreeReq = determineDegreeRequirement(major.getName(), major.getEnglishName());

            // 3.2 根据 Tier 和 学位要求 获取分数线
            double requiredGpa = getRequiredGpa(studentTier, degreeReq);

            // 3.3 计算匹配度
            double matchScore = calculateMatchScore(studentInfo, requiredGpa, studentTier, degreeReq);
            String matchLevel = determineMatchLevel(matchScore);
            String matchReason = generateMatchReason(studentInfo, studentTier, requiredGpa, degreeReq, matchScore);

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
            result.setAlgorithmStrategy("UK_NOTTINGHAM_MATCHING_ALGORITHM");

            try {
                result.setStudentInfoSnapshot(objectMapper.writeValueAsString(studentInfo));
            } catch (Exception e) {
                result.setStudentInfoSnapshot("{}");
            }

            results.add(result);
        }

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
     * 判断院校 Tier
     * 逻辑：
     * 1. 在 Tier 1 名单 -> Tier 1
     * 2. 包含 "大学" 且不含 "学院" (除特例) -> Tier 2
     * 3. 包含 "学院" 或 "分校" -> Tier 3
     */
    private String getUniversityTier(String schoolName) {
        if (schoolName == null || schoolName.trim().isEmpty())
            return "Tier 3";

        // 1. Check Tier 1
        for (String key : TIER_1_UNIVERSITIES) {
            if (schoolName.contains(key)) {
                return "Tier 1";
            }
        }

        // 2. Check Tier 3 (Independent Colleges)
        // 如果名字里包含 "学院" 且前面有大学名 (e.g. 厦门大学嘉庚学院)，通常是独立学院
        // 简单判断：如果不包含 "大学" 字样，或者包含 "职业技术"，或者包含 "分校"
        if (schoolName.contains("分校") || schoolName.contains("独立学院")) {
            return "Tier 3";
        }
        // 特殊处理：如果是 "XX大学XX学院"，极大概率是 Tier 3
        // 如果只是 "XX学院" (如上海立信会计金融学院)，可能是公办二本，属于 Tier 2
        // 这里做一个简化假设：如果不在 Tier 1，我们默认为 Tier 2 (双非公办)，除非有明显独立学院特征

        if (schoolName.endsWith("学院") && schoolName.length() > 8 && schoolName.contains("大学")) {
            // 这种通常是独立学院
            return "Tier 3";
        }

        // 3. Default to Tier 2 (普通双非一本/二本)
        return "Tier 2";
    }

    /**
     * 确定专业学位要求
     * High 2:1 / 2:1 / 2:2
     */
    private String determineDegreeRequirement(String majorName, String majorEnName) {
        String name = (majorName + " " + (majorEnName != null ? majorEnName : "")).toLowerCase();

        // 1. High 2:1 / 1st (法学)
        // 诺丁汉法学院要求较高
        if (name.contains("law") || name.contains("llm") || name.contains("法学")) {
            return "High 2:1";
        }

        // 2. 2:2 (工程、部分理科)
        if (name.contains("engineering") || name.contains("civil") ||
                name.contains("mechanical") || name.contains("electrical") ||
                name.contains("biology") || name.contains("chemistry")) {
            return "2:2";
        }

        // 3. 2:1 (默认：商科、计算机、传媒、教育、人文)
        return "2:1";
    }

    /**
     * 获取分数线 (基于图片)
     */
    private double getRequiredGpa(String tier, String degreeReq) {
        switch (degreeReq) {
            case "High 2:1": // High 2:1 or 1st
                if ("Tier 1".equals(tier))
                    return 75.0;
                if ("Tier 2".equals(tier))
                    return 78.0;
                return 82.0; // Tier 3

            case "2:1": // Standard 2:1
                if ("Tier 1".equals(tier))
                    return 72.0;
                if ("Tier 2".equals(tier))
                    return 75.0;
                return 80.0; // Tier 3

            case "2:2": // 2:2
                if ("Tier 1".equals(tier))
                    return 68.0;
                if ("Tier 2".equals(tier))
                    return 72.0;
                return 75.0; // Tier 3

            default:
                return 80.0;
        }
    }

    private double calculateMatchScore(MatchingRequest.StudentInfoDTO student, double required, String tier,
            String degreeReq) {
        if (student.getGpa() == null)
            return 0.0;
        double gpa = convertGpaToPercentage(student.getGpa(), student.getGpaScale());
        double score = 0.0;

        // 1. 均分评分
        if (gpa >= required + 2) {
            score += 85.0; // 稳妥
        } else if (gpa >= required) {
            score += 75.0; // 达标
        } else if (gpa >= required - 2) {
            score += 60.0; // 冲刺 (诺丁汉对分数有一定Argue空间，特别是Tier 1学生)
        } else {
            score += 30.0; // 差距较大
        }

        // 2. 背景加分
        if ("Tier 1".equals(tier))
            score += 10.0;
        if ("Tier 2".equals(tier))
            score += 5.0;

        // 3. 难度加权
        if ("High 2:1".equals(degreeReq))
            score -= 5.0; // 法学等专业竞争激烈

        return Math.min(100.0, score);
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

    private String determineMatchLevel(double score) {
        if (score >= 88)
            return "保底";
        if (score >= 75)
            return "稳妥";
        if (score >= 60)
            return "冲刺";
        return "不建议";
    }

    private String generateMatchReason(MatchingRequest.StudentInfoDTO student, String tier, double required,
            String degreeReq, double score) {
        StringBuilder sb = new StringBuilder();
        sb.append("【诺丁汉大学】");

        sb.append("您的本科院校被判定为 ").append(tier).append("。");
        // 解释 Tier
        if ("Tier 1".equals(tier))
            sb.append("(985/211/双一流)");
        else if ("Tier 2".equals(tier))
            sb.append("(普通双非一本/二本)");
        else
            sb.append("(独立学院/民办)");

        sb.append("\n申请专业要求英国 ").append(degreeReq).append(" 学位，");
        sb.append("针对 ").append(tier).append(" 的均分要求为 ").append((int) required).append("%。");

        double gpaPercent = convertGpaToPercentage(student.getGpa(), student.getGpaScale());
        String gpaText = student.getGpaScale() == null || student.getGpaScale().trim().isEmpty()
                ? String.format("%.1f", student.getGpa())
                : String.format("%.2f/%s", student.getGpa(), student.getGpaScale().trim());
        sb.append("\n您的均分为 ").append(String.format("%.1f", gpaPercent)).append("%（").append(gpaText).append("）。");

        if (gpaPercent >= required) {
            sb.append(" 成绩达标，录取希望很大。");
        } else if (gpaPercent >= required - 2) {
            sb.append(" 成绩略有差距，建议提升背景或作为冲刺申请。");
        } else {
            sb.append(" 成绩未达标，建议考虑其他学校。");
        }

        return sb.toString();
    }

    private double calculateAdmissionProbability(double score) {
        if (score >= 88)
            return 0.95;
        if (score >= 75)
            return 0.85;
        if (score >= 60)
            return 0.45;
        return 0.10;
    }

    private double calculateRecommendationIndex(double score) {
        return Math.min(1.0, score / 100.0);
    }
}
