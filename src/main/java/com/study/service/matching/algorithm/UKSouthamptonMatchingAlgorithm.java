package com.study.service.matching.algorithm;

import com.study.service.matching.MatchingDataService;
import com.study.service.matching.MatchingResult;
import com.study.service.matching.dto.MatchingRequest;
import com.study.service.matching.rules.UkSchoolRuleEngineService;
import com.study.service.schools.School;
import com.study.service.major.Major;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 南安普顿大学 (University of Southampton) 2026 匹配算法
 * 
 * 规则来源：官方 2026 PGT Admissions Criteria 图片
 * 
 * 1. 院校分级 (Tiers):
 * - Tier A: 985院校
 * - Tier B: 211院校 + 软科Top 300
 * - Tier C: 软科301-600 + 头部民办/独立
 * - Tier D: 其他院校
 * 
 * 2. 均分要求 (2:1 Degree):
 * - Tier A: 70%
 * - Tier B: 75%
 * - Tier C: 80%
 * - Tier D: 88%
 * 
 * 3. 特殊限制 (Yellow Box):
 * - Tier D 学生不可申请 ECS (电子与计算机科学) 和 Engineering (工程) 学院的课程。
 */
@Service
@SuppressWarnings("unused")
public class UKSouthamptonMatchingAlgorithm implements SchoolMatchingStrategy {

    private static final Logger log = LoggerFactory.getLogger(UKSouthamptonMatchingAlgorithm.class);

    @Autowired
    private MatchingDataService dataService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UkSchoolRuleEngineService ruleEngineService;

    // =================================================================================
    // Tier A: 985 Universities + Top Specialist
    // =================================================================================
    // =================================================================================
    private static final Set<String> TIER_A_UNIVERSITIES = new HashSet<>();
    static {
        // 辅助方法添加
        addTierA("Beijing University of Aeronautics and Astronautics", "Beihang University", "北京航空航天大学");
        addTierA("Beijing Institute of Technology", "北京理工大学");
        addTierA("Beijing Normal University", "北京师范大学");
        addTierA("Central South University", "中南大学");
        addTierA("China Agricultural University", "中国农业大学");
        addTierA("Chongqing University", "重庆大学");
        addTierA("Dalian University of Technology", "大连理工大学");
        addTierA("East China Normal University", "华东师范大学");
        addTierA("Fudan University", "复旦大学");
        addTierA("Harbin Institute of Technology", "哈尔滨工业大学");
        addTierA("Harbin Institute of Technology, Shenzhen", "哈尔滨工业大学（深圳）");
        addTierA("Harbin Institute of Technology, Weihai", "哈尔滨工业大学（威海）");
        addTierA("Huazhong University of Science and Technology", "华中科技大学");
        addTierA("Hunan University", "湖南大学");
        addTierA("Jilin University", "吉林大学");
        addTierA("Lanzhou University", "兰州大学");
        addTierA("Minzu University of China", "中央民族大学");
        addTierA("Nanjing University", "南京大学");
        addTierA("Nankai University", "南开大学");
        addTierA("National University of Defense Technology", "国防科技大学");
        addTierA("Northeastern University", "东北大学");
        addTierA("Northeastern University at Qinhuangdao", "东北大学秦皇岛分校");
        addTierA("Northwest A&F University", "西北农林科技大学");
        addTierA("Northwestern Polytechnical University", "西北工业大学");
        addTierA("Ocean University of China", "中国海洋大学");
        addTierA("Peking University", "北京大学");
        addTierA("Renmin University of China", "中国人民大学");
        addTierA("Shandong University", "山东大学");
        addTierA("Shanghai Jiao Tong University", "上海交通大学");
        addTierA("Sichuan University", "四川大学");
        addTierA("South China University of Technology", "华南理工大学");
        addTierA("Southeast University", "东南大学");
        addTierA("Southern University of Science and Technology", "南方科技大学");
        addTierA("Sun Yat-sen University", "中山大学");
        addTierA("Tianjin University", "天津大学");
        addTierA("Tongji University", "同济大学");
        addTierA("Tsinghua University", "清华大学");
        addTierA("University of Chinese Academy of Sciences", "中国科学院大学");
        addTierA("University of Electronic Science and Technology of China", "电子科技大学");
        addTierA("University of Science and Technology of China", "中国科学技术大学");
        addTierA("Westlake University", "西湖大学");
        addTierA("Wuhan University", "武汉大学");
        addTierA("Xiamen University", "厦门大学");
        addTierA("Xiamen University of Malaysia", "厦门大学马来西亚分校");
        addTierA("Xi'an Jiaotong University", "西安交通大学");
        addTierA("Zhejiang University", "浙江大学");
    }

    private static void addTierA(String... names) {
        for (String name : names) {
            if (name != null && !name.isEmpty()) {
                TIER_A_UNIVERSITIES.add(name);
                // 同时添加去空格版本，防止输入误差
                TIER_A_UNIVERSITIES.add(name.replace(" ", ""));
            }
        }
    }

    // =================================================================================
    // Tier B: 211 Universities (Non-985) + Top 300 (Simplified as Strong Public)
    // =================================================================================
    private static final Set<String> TIER_B_UNIVERSITIES = new HashSet<>();
    static {
        // 211 (部分示例，实际应包含所有非985的211)
        String[] list211 = {
                "北京科技大学", "北京化工大学", "北京邮电大学", "中国传媒大学", "中央财经大学",
                "对外经济贸易大学", "华北电力大学", "上海大学", "东华大学", "上海财经大学",
                "南京理工大学", "南京航空航天大学", "苏州大学", "江南大学", "河海大学",
                "武汉理工大学", "华中师范大学", "西南交通大学", "西南财经大学", "西安电子科技大学",
                "暨南大学", "华南师范大学", "郑州大学", "福州大学", "南昌大学", "西北大学"
        };
        // 软科 Top 300 中常见的双非强校 (示例)
        String[] top300DoubleNon = {
                "深圳大学", "南方科技大学", "江苏大学", "扬州大学", "浙江工业大学",
                "南京工业大学", "杭州电子科技大学", "广东工业大学", "上海理工大学",
                "首都师范大学", "燕山大学", "青岛大学", "上海师范大学", "昆明理工大学"
        };
        Collections.addAll(TIER_B_UNIVERSITIES, list211);
        Collections.addAll(TIER_B_UNIVERSITIES, top300DoubleNon);
    }

    // =================================================================================
    // Tier C: Rank 301-600 (普通公办本科) + Top Private
    // =================================================================================
    // 代码逻辑中，如果既不是 Tier A 也不是 Tier B，且不含“学院/独立”字样，通常归为 Tier C
    // 如果包含“学院”但不是公办，或者明确的独立学院，归为 Tier D

    @Override
    public String getSchoolName() {
        return "南安普顿大学";
    }

    @Override
    public String getRegion() {
        return "英国";
    }

    @Override
    public List<MatchingResult> computeMatchingResults(MatchingRequest request) {
        List<MatchingResult> results = new ArrayList<>();

        School school = dataService.getSchoolByName(getSchoolName());
        if (school == null) {
            List<School> ukSchools = dataService.getSchoolsByRegion("英国");
            school = ukSchools.stream()
                    .filter(s -> s.getEnglishName() != null
                            && s.getEnglishName().equalsIgnoreCase("University of Southampton"))
                    .findFirst()
                    .orElse(null);
            if (school == null) {
                return results;
            }
        }

        MatchingRequest.StudentInfoDTO studentInfo = request.getStudentInfo();
        String undergradSchool = studentInfo.getUndergraduateSchool();

        String studentTier = ruleEngineService.resolveGroupCodeOrNull(school.getId(), undergradSchool);
        if (studentTier == null) {
            studentTier = getUniversityTier(undergradSchool);
        }

        List<Major> majors = dataService.getMajorsBySchoolId(school.getId());
        List<String> majorTokens = UKSingleSchoolMatchingAlgorithmBase.extractMajorTokens(request);
        majors = UKSingleSchoolMatchingAlgorithmBase.selectMajorsByTokens(majors, majorTokens, 10);

        for (Major major : majors) {
            String majorName = major.getName();
            String majorEnName = major.getEnglishName();

            // 2. 检查 Tier D 限制 (ECS & Engineering)
            boolean isRestricted = isRestrictedMajor(majorName, majorEnName);

            // 3. 确定学位要求 (默认为 2:1)
            String degreeReq = determineDegreeRequirement(majorName, majorEnName);

            String majorCategory = isRestricted ? "ECS_ENGINEERING" : null;
            UkSchoolRuleEngineService.RequirementDecision decision = ruleEngineService.resolveRequirementOrNull(
                    school.getId(),
                    studentTier,
                    degreeReq,
                    majorCategory);
            if (decision != null && decision.isReject()) {
                createRejectionResult(results, studentInfo, school, major, studentTier,
                        "院校背景不符合该专业申请要求。");
                continue;
            }

            double requiredGpa;
            if (decision != null && decision.getRequiredScore() != null) {
                requiredGpa = decision.getRequiredScore();
            } else {
                if ("Tier D".equals(studentTier) && isRestricted) {
                    createRejectionResult(results, studentInfo, school, major, studentTier,
                            "Tier D 院校背景不被电子与计算机科学学院(ECS)及工程学院接受。");
                    continue;
                }
                requiredGpa = getRequiredGpa(studentTier, degreeReq);
            }

            // 5. 计算匹配度
            double matchScore = calculateMatchScore(studentInfo, requiredGpa, studentTier,
                    isRestricted);

            MatchingResult result = new MatchingResult();
            result.setUserId(studentInfo.getUserId());
            result.setSchoolId(school.getId());
            result.setSchoolName(school.getName());
            result.setMajorName(majorName);
            result.setMatchScore(matchScore);
            result.setMatchLevel(determineMatchLevel(matchScore));
            result.setAdmissionProbability(calculateAdmissionProbability(matchScore));
            result.setRecommendationIndex(calculateRecommendationIndex(matchScore));
            result.setRegion("英国");
            result.setAlgorithmStrategy("UK_SOUTHAMPTON_MATCHING_ALGORITHM");

            String analysis = generateAnalysis(studentInfo, studentTier, degreeReq, requiredGpa, matchScore,
                    isRestricted);
            result.setRiskAnalysis(analysis);
            result.setMatchReason(analysis);

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
     * 判定院校 Tier
     */
    private String getUniversityTier(String schoolName) {
        if (schoolName == null || schoolName.trim().isEmpty())
            return "Tier D";

        // Tier A
        for (String s : TIER_A_UNIVERSITIES) {
            if (schoolName.contains(s))
                return "Tier A";
        }

        // Tier B
        for (String s : TIER_B_UNIVERSITIES) {
            if (schoolName.contains(s))
                return "Tier B";
        }

        // Tier C vs Tier D
        // 逻辑：如果是普通公办本科(通常以"大学"结尾且不含"学院/分校") -> Tier C
        // 如果包含 "学院"、"分校"、"技术" 等字眼，或者完全无法识别 -> Tier D
        // 这是一个简化判断，实际需要完整名单

        if (schoolName.endsWith("大学") && !schoolName.contains("分校") && !schoolName.contains("学院")) {
            return "Tier C";
        }

        if (schoolName.contains("独立学院") || schoolName.contains("职业技术")) {
            return "Tier D";
        }

        // 包含 "学院" 的公办二本通常在 Tier C，但为了保险，如果不在明确的List里，
        // 且名字像独立学院 (XX大学XX学院)，归为 Tier D。如果是纯 "XX学院" (如XX师范学院)，可能是 Tier C。
        if (schoolName.contains("大学") && schoolName.contains("学院")) {
            return "Tier D"; // 独立学院特征
        }

        return "Tier C"; // 剩下的普通公办学院归为 C
    }

    /**
     * 判断是否为限制专业 (ECS & Engineering)
     * 依据：Qualifications awarded by institutions in Tier D are not accepted for
     * entry to programmes in the School of Electronics and Computer Science and the
     * School of Engineering.
     */
    private boolean isRestrictedMajor(String name, String enName) {
        String combined = (name + " " + (enName == null ? "" : enName)).toLowerCase();

        // 关键词匹配
        if (combined.contains("computer science") || combined.contains("计算机") ||
                combined.contains("electronics") || combined.contains("电子") ||
                combined.contains("software") || combined.contains("软件") ||
                combined.contains("data science") || combined.contains("数据科学") ||
                combined.contains("artificial intelligence") || combined.contains("人工智能") ||
                combined.contains("cyber security")) {
            return true; // ECS School
        }

        if (combined.contains("engineering") || combined.contains("工程") ||
                combined.contains("civil") || combined.contains("土木") ||
                combined.contains("mechanical") || combined.contains("机械") ||
                combined.contains("aerospace") || combined.contains("航空")) {
            return true; // Engineering School
        }

        return false;
    }

    /**
     * 确定学位要求 (南安普顿绝大多数硕士要求 2:1)
     */
    private String determineDegreeRequirement(String name, String enName) {
        // 部分理工科可能接受 2:2，但 ECS 通常要 2:1
        // 默认返回 2:1，因为这是南安的主流要求
        return "2:1";
    }

    /**
     * 获取均分要求 (根据图片表格)
     */
    private double getRequiredGpa(String tier, String degreeReq) {
        if ("2:1".equals(degreeReq)) {
            switch (tier) {
                case "Tier A":
                    return 70.0;
                case "Tier B":
                    return 75.0;
                case "Tier C":
                    return 80.0;
                case "Tier D":
                    return 88.0;
            }
        } else if ("2:2".equals(degreeReq)) {
            switch (tier) {
                case "Tier A":
                    return 65.0;
                case "Tier B":
                    return 70.0;
                case "Tier C":
                    return 75.0;
                case "Tier D":
                    return 83.0;
            }
        }
        return 90.0; // Fallback
    }

    private double getStudentGpa(MatchingRequest.StudentInfoDTO student) {
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

    private double calculateMatchScore(MatchingRequest.StudentInfoDTO student, double required, String tier,
            boolean isRestricted) {
        // 如果是 Tier D 且是受限专业，前面已经拦截，这里是防御性逻辑
        if ("Tier D".equals(tier) && isRestricted)
            return 0.0;

        double gpa = getStudentGpa(student);
        double score = 0.0;

        if (gpa >= required) {
            score += 80.0;
            if (gpa >= required + 2)
                score += 10.0; // 稳妥
            if (gpa >= required + 5)
                score += 10.0; // 极稳
        } else if (gpa >= required - 2) {
            score += 55.0; // 冲刺
        } else {
            score += 20.0; // 差距较大
        }

        // 南安普顿对 Tier A/B 比较友好
        if ("Tier A".equals(tier) || "Tier B".equals(tier)) {
            score += 5.0;
        }

        return Math.min(100.0, score);
    }

    private void createRejectionResult(List<MatchingResult> results, MatchingRequest.StudentInfoDTO student,
            School school, Major major, String tier, String reason) {
        MatchingResult result = new MatchingResult();
        result.setUserId(student.getUserId());
        result.setSchoolId(school.getId());
        result.setSchoolName(school.getName());
        result.setMajorName(major.getName());
        result.setMatchScore(0.0);
        result.setMatchLevel("不符合申请资格");
        result.setAdmissionProbability(0.0);
        result.setRecommendationIndex(0.0);
        result.setRegion("英国");
        result.setAlgorithmStrategy("UK_SOUTHAMPTON_MATCHING_ALGORITHM");
        result.setRiskAnalysis(reason);
        result.setMatchReason(reason);
        results.add(result);
    }

    private String generateAnalysis(MatchingRequest.StudentInfoDTO student, String tier, String degreeReq,
            double required, double score, boolean isRestricted) {
        StringBuilder sb = new StringBuilder();
        sb.append("【南安普顿大学】\n");
        sb.append("1. 院校背景：您的院校被判定为 ").append(tier).append("。\n");

        if (isRestricted) {
            sb.append("   (注：您申请的是工程/计算机类专业，该类专业不接受 Tier D 院校申请)\n");
        }

        sb.append("2. 专业要求：英国 ").append(degreeReq).append(" 学位。\n");
        sb.append("3. 均分门槛：针对 ").append(tier).append(" 学生的最低要求为 ").append((int) required).append("%。\n");
        double gpaPercent = convertGpaToPercentage(student.getGpa(), student.getGpaScale());
        String gpaText = student.getGpa() == null ? "未填写"
                : (student.getGpaScale() == null || student.getGpaScale().trim().isEmpty()
                        ? String.format("%.1f", student.getGpa())
                        : String.format("%.2f/%s", student.getGpa(), student.getGpaScale().trim()));
        sb.append("4. 您的成绩：").append(String.format("%.1f", gpaPercent)).append("%（").append(gpaText).append("）。\n");

        if (score >= 80) {
            sb.append("结果：成绩达标，背景匹配度高，录取希望很大。");
        } else if (score >= 60) {
            sb.append("结果：成绩略有差距，属于冲刺范围。");
        } else {
            sb.append("结果：成绩差距较大，建议考虑其他院校。");
        }

        return sb.toString();
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

    private double calculateAdmissionProbability(double score) {
        if (score >= 90)
            return 0.95;
        if (score >= 80)
            return 0.85;
        if (score >= 60)
            return 0.45;
        return 0.10;
    }

    private double calculateRecommendationIndex(double score) {
        return Math.min(1.0, score / 100.0);
    }
}
