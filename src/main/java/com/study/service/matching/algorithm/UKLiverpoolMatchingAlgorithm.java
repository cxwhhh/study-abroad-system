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
 * 利物浦大学 (University of Liverpool) 匹配算法
 * 适用入学季：2026 秋季
 * 
 * 规则来源：官方录取要求图表
 * 
 * 1. 院校分级 (Tier)：
 * - Tier 1: 国家双一流院校。
 * - Tier 2: 所有双非院校(无名单) 及 已转设的独立学院。
 * - Tier 3: 未转设的独立学院(无名单)。
 * 
 * 2. 均分要求：
 * - 通用专业: T1 65%, T2 70%, T3 78%
 * - 计算机 (CS): T1 65%, T2 70%, T3 不接受
 * - MBA: T1 70%, T2 75%, T3 83%
 * - 心理学: T1 70%, T2 75%, T3 83% (取区间下限)
 */
@Service
@SuppressWarnings("unused")
public class UKLiverpoolMatchingAlgorithm implements SchoolMatchingStrategy {

    private static final Logger log = LoggerFactory.getLogger(UKLiverpoolMatchingAlgorithm.class);

    @Autowired
    private MatchingDataService dataService;

    @Autowired
    private ObjectMapper objectMapper;

    // =================================================================================
    // 国家“双一流”建设高校名单 (Tier 1)
    // =================================================================================
    private static final Set<String> DOUBLE_FIRST_CLASS_UNIVERSITIES = new HashSet<>();
    static {
        // 北京
        addTier1("Peking University", "北京大学");
        addTier1("Renmin University of China", "中国人民大学");
        addTier1("Tsinghua University", "清华大学");
        addTier1("Beihang University", "北京航空航天大学");
        addTier1("Beijing Institute of Technology", "北京理工大学");
        addTier1("China Agricultural University", "中国农业大学");
        addTier1("Beijing Normal University", "北京师范大学");
        addTier1("Minzu University of China", "中央民族大学");
        addTier1("Nankai University", "南开大学");
        addTier1("Tianjin University", "天津大学");
        addTier1("Dalian University of Technology", "大连理工大学");
        addTier1("Jilin University", "吉林大学");
        addTier1("Harbin Institute of Technology", "哈尔滨工业大学");
        addTier1("Fudan University", "复旦大学");
        addTier1("Tongji University", "同济大学");
        addTier1("Shanghai Jiao Tong University", "上海交通大学");
        addTier1("East China Normal University", "华东师范大学");
        addTier1("Nanjing University", "南京大学");
        addTier1("Southeast University", "东南大学");
        addTier1("Zhejiang University", "浙江大学");
        addTier1("University of Science and Technology of China", "中国科学技术大学");
        addTier1("Xiamen University", "厦门大学");
        addTier1("Shandong University", "山东大学");
        addTier1("Ocean University of China", "中国海洋大学");
        addTier1("Wuhan University", "武汉大学");
        addTier1("Huazhong University of Science and Technology", "华中科技大学");
        addTier1("Central South University", "中南大学");
        addTier1("Sun Yat-sen University", "中山大学");
        addTier1("South China University of Technology", "华南理工大学");
        addTier1("Sichuan University", "四川大学");
        addTier1("Chongqing University", "重庆大学");
        addTier1("University of Electronic Science and Technology of China", "电子科技大学");
        addTier1("Xi'an Jiaotong University", "西安交通大学");
        addTier1("Northwestern Polytechnical University", "西北工业大学");
        addTier1("Lanzhou University", "兰州大学");
        addTier1("National University of Defense Technology", "国防科技大学");
        addTier1("Northeastern University", "东北大学");
        addTier1("Zhengzhou University", "郑州大学");
        addTier1("Hunan University", "湖南大学");
        addTier1("Yunnan University", "云南大学");
        addTier1("Northwest A&F University", "西北农林科技大学");
        addTier1("Xinjiang University", "新疆大学");
        addTier1("Beijing Jiaotong University", "北京交通大学");
        addTier1("Beijing University of Technology", "北京工业大学");
        addTier1("University of Science and Technology Beijing", "北京科技大学");
        addTier1("Beijing University of Chemical Technology", "北京化工大学");
        addTier1("Beijing University of Posts and Telecommunications", "北京邮电大学");
        addTier1("Beijing Forestry University", "北京林业大学");
        addTier1("Peking Union Medical College", "北京协和医学院");
        addTier1("Beijing University of Chinese Medicine", "北京中医药大学");
        addTier1("Capital Normal University", "首都师范大学");
        addTier1("Beijing Foreign Studies University", "北京外国语大学");
        addTier1("Communication University of China", "中国传媒大学");
        addTier1("Central University of Finance and Economics", "中央财经大学");
        addTier1("University of International Business and Economics", "对外经济贸易大学");
        addTier1("China Foreign Affairs University", "外交学院");
        addTier1("People's Public Security University of China", "中国人民公安大学");
        addTier1("Beijing Sport University", "北京体育大学");
        addTier1("Central Conservatory of Music", "中央音乐学院");
        addTier1("China Conservatory of Music", "中国音乐学院");
        addTier1("Central Academy of Fine Arts", "中央美术学院");
        addTier1("The Central Academy of Drama", "中央戏剧学院");
        addTier1("China University of Political Science and Law", "中国政法大学");
        addTier1("Tianjin University of Technology", "天津工业大学"); // Note: Tiangong Univ now
        addTier1("Tianjin Medical University", "天津医科大学");
        addTier1("Tianjin University of Traditional Chinese Medicine", "天津中医药大学");
        addTier1("North China Electric Power University", "华北电力大学");
        addTier1("Hebei University of Technology", "河北工业大学");
        addTier1("Taiyuan University of Technology", "太原理工大学");
        addTier1("Inner Mongolia University", "内蒙古大学");
        addTier1("Liaoning University", "辽宁大学");
        addTier1("Dalian Maritime University", "大连海事大学");
        addTier1("Yanbian University", "延边大学");
        addTier1("Northeast Normal University", "东北师范大学");
        addTier1("Harbin Engineering University", "哈尔滨工程大学");
        addTier1("Northeast Forestry University", "东北林业大学");
        addTier1("East China University of Science and Technology", "华东理工大学");
        addTier1("Donghua University", "东华大学");
        addTier1("Shanghai Ocean University", "上海海洋大学");
        addTier1("Shanghai University", "上海大学");
        addTier1("Shanghai University of Traditional Chinese Medicine", "上海中医药大学");
        addTier1("Shanghai University of Sport", "上海体育学院");
        addTier1("Shanghai Conservatory of Music", "上海音乐学院");
        addTier1("Shanghai University of Finance and Economics", "上海财经大学");
        addTier1("Soochow University", "苏州大学");
        addTier1("Nanjing Medical University", "南京医科大学");
        addTier1("Nanjing University of Aeronautics and Astronautics", "南京航空航天大学");
        addTier1("Nanjing University of Science and Technology", "南京理工大学");
        addTier1("China University of Mining and Technology", "中国矿业大学");
        addTier1("Nanjing University of Posts and Telecommunications", "南京邮电大学");
        addTier1("Hohai University", "河海大学");
        addTier1("Jiangnan University", "江南大学");
        addTier1("Nanjing Forestry University", "南京林业大学");
        addTier1("Nanjing University of Information Science & Technology", "南京信息工程大学");
        addTier1("Nanjing Agricultural University", "南京农业大学");
        addTier1("Nanjing University of Chinese Medicine", "南京中医药大学");
        addTier1("China Pharmaceutical University", "中国药科大学");
        addTier1("Nanjing Normal University", "南京师范大学");
        addTier1("China Academy of Art", "中国美术学院");
        addTier1("Anhui University", "安徽大学");
        addTier1("University of Science and Technology of China", "中国科学技术大学");
        addTier1("Fuzhou University", "福州大学");
        addTier1("Nanchang University", "南昌大学");
        addTier1("Henan University", "河南大学");
        addTier1("China University of Geosciences", "中国地质大学"); // Wuhan & Beijing
        addTier1("Wuhan University of Technology", "武汉理工大学");
        addTier1("Huazhong Agricultural University", "华中农业大学");
        addTier1("Central China Normal University", "华中师范大学");
        addTier1("Zhongnan University of Economics and Law", "中南财经政法大学");
        addTier1("Hunan Normal University", "湖南师范大学");
        addTier1("South China Normal University", "华南师范大学");
        addTier1("Jinan University", "暨南大学");
        addTier1("Guangzhou University of Chinese Medicine", "广州中医药大学");
        addTier1("Guangzhou Medical University", "广州医科大学");
        addTier1("Southern University of Science and Technology", "南方科技大学");
        addTier1("Guangxi University", "广西大学");
        addTier1("Hainan University", "海南大学");
        addTier1("Chongqing University", "重庆大学");
        addTier1("Southwest University", "西南大学");
        addTier1("Sichuan Agricultural University", "四川农业大学");
        addTier1("Chengdu University of Technology", "成都理工大学");
        addTier1("Chengdu University of Traditional Chinese Medicine", "成都中医药大学");
        addTier1("Southwest Jiaotong University", "西南交通大学");
        addTier1("Southwest Petroleum University", "西南石油大学");
        addTier1("Southwestern University of Finance and Economics", "西南财经大学");
        addTier1("Guizhou University", "贵州大学");
        addTier1("Tibet University", "西藏大学");
        addTier1("Northwest University", "西北大学");
        addTier1("Xi'an University of Architecture and Technology", "西安建筑科技大学"); // Non-Double 1st but often treated
                                                                                 // high, but sticking to strict list
        addTier1("Xidian University", "西安电子科技大学");
        addTier1("Shaanxi Normal University", "陕西师范大学");
        addTier1("Qinghai University", "青海大学");
        addTier1("Ningxia University", "宁夏大学");
        addTier1("Shihezi University", "石河子大学");
        addTier1("China University of Petroleum", "中国石油大学");
        addTier1("University of Chinese Academy of Sciences", "中国科学院大学");
        addTier1("ShanghaiTech University", "上海科技大学");
        addTier1("Nanjing Medical University", "南京医科大学");
        addTier1("Shanxi University", "山西大学");
        addTier1("Xiangtan University", "湘潭大学");
        addTier1("Yangzhou University", "扬州大学"); // Sometimes counted, sticking to Double First Class list
    }

    private static void addTier1(String en, String cn) {
        DOUBLE_FIRST_CLASS_UNIVERSITIES.add(en);
        DOUBLE_FIRST_CLASS_UNIVERSITIES.add(cn);
    }

    @Override
    public String getSchoolName() {
        return "利物浦大学";
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
                            && s.getEnglishName().equalsIgnoreCase("University of Liverpool"))
                    .findFirst()
                    .orElse(null);
            if (school == null) {
                log.warn("未在数据库中找到利物浦大学");
                return results;
            }
        }

        MatchingRequest.StudentInfoDTO studentInfo = request.getStudentInfo();
        String undergradSchool = studentInfo.getUndergraduateSchool();

        // 1. 确定院校 Tier
        String studentTier = getUniversityTier(undergradSchool);

        List<Major> majors = dataService.getMajorsBySchoolId(school.getId());
        List<String> majorTokens = UKSingleSchoolMatchingAlgorithmBase.extractMajorTokens(request);
        majors = UKSingleSchoolMatchingAlgorithmBase.selectMajorsByTokens(majors, majorTokens, 10);
        for (Major major : majors) {
            // 2. 确定专业分类
            String majorCategory = getMajorCategory(major);

            // 3. 获取 GPA 要求
            double requiredGpa = getRequiredGpa(studentTier, majorCategory);

            // 4. 计算匹配分
            double matchScore = calculateMatchScore(studentInfo, requiredGpa, studentTier, majorCategory);

            MatchingResult result = new MatchingResult();
            result.setUserId(studentInfo.getUserId());
            result.setSchoolId(school.getId());
            result.setSchoolName(school.getName());
            result.setMajorName(major.getName());
            result.setMatchScore(matchScore);
            result.setMatchLevel(determineMatchLevel(matchScore));
            result.setAdmissionProbability(calculateAdmissionProbability(matchScore));
            result.setRecommendationIndex(calculateRecommendationIndex(matchScore));
            result.setRegion("英国");
            result.setAlgorithmStrategy("UK_LIVERPOOL_MATCHING_ALGORITHM");

            String analysis = generateRiskAnalysis(studentInfo, studentTier, majorCategory, requiredGpa, matchScore);
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
     * 院校分级逻辑
     * Tier 1: 双一流
     * Tier 3: 独立学院 (包含 "学院" 且不在 Tier 1，通常格式为 XX大学XX学院，不含已转设的公办)
     * Tier 2: 其他所有院校 (双非公办 + 已转设独立学院)
     */
    private String getUniversityTier(String schoolName) {
        if (schoolName == null || schoolName.trim().isEmpty())
            return "UNKNOWN";

        // 1. 检查 Tier 1 (双一流)
        for (String key : DOUBLE_FIRST_CLASS_UNIVERSITIES) {
            if (schoolName.contains(key) || key.contains(schoolName)) {
                return "Tier 1";
            }
        }

        // 2. 检查 Tier 3 (未转设独立学院)
        // 逻辑：名称包含 "学院" 且 包含 "大学"，通常是 "XX大学XX学院" 格式
        // 排除一些特殊的公办名字包含学院的 (如外交学院，已在Tier 1处理)
        // 这是一个简化逻辑，实际应有一个独立学院名单。
        // 利物浦政策：Tier 2 包括 "已转设的独立学院"。
        // 假设输入是标准中文全称
        if (schoolName.contains("大学") && schoolName.contains("学院") && !schoolName.endsWith("大学")) {
            // 排除一些特殊情况，比如 "中国科学院大学" (已在T1)，"xx医学院" (通常T2)
            // 独立学院特征： 前面是大学名，后面是学院名
            return "Tier 3";
        }

        // 3. 默认为 Tier 2 (普通双非 / 已转设)
        return "Tier 2";
    }

    /**
     * 专业分类
     */
    private String getMajorCategory(Major major) {
        String name = major.getName().toLowerCase();
        String engName = major.getEnglishName() == null ? "" : major.getEnglishName().toLowerCase();

        // 1. Computer Science
        if (name.contains("computer") || engName.contains("computer") ||
                name.contains("data science") || engName.contains("data science") ||
                name.contains("artificial intelligence") || engName.contains("artificial intelligence") ||
                name.contains("计算机")) {
            return "CS";
        }

        // 2. MBA (Strictly MBA)
        if ((name.contains("mba") || engName.contains("mba")) ||
                (name.contains("工商管理硕士"))) {
            return "MBA";
        }

        // 3. Psychology
        if (name.contains("psychology") || engName.contains("psychology") || name.contains("心理")) {
            return "PSYCHOLOGY";
        }

        // 4. General (All others)
        return "GENERAL";
    }

    /**
     * 获取分数线
     */
    private double getRequiredGpa(String tier, String category) {
        if ("UNKNOWN".equals(tier))
            return 999.0;

        switch (category) {
            case "CS":
                if ("Tier 1".equals(tier))
                    return 65.0;
                if ("Tier 2".equals(tier))
                    return 70.0;
                return 999.0; // Tier 3 Not Accepted for CS

            case "MBA":
                if ("Tier 1".equals(tier))
                    return 70.0;
                if ("Tier 2".equals(tier))
                    return 75.0;
                if ("Tier 3".equals(tier))
                    return 83.0;
                return 999.0;

            case "PSYCHOLOGY":
                // Image says T1 70-72, T2 75-77, T3 83-85. Taking lower bound.
                if ("Tier 1".equals(tier))
                    return 70.0;
                if ("Tier 2".equals(tier))
                    return 75.0;
                if ("Tier 3".equals(tier))
                    return 83.0;
                return 999.0;

            case "GENERAL":
            default:
                if ("Tier 1".equals(tier))
                    return 65.0;
                if ("Tier 2".equals(tier))
                    return 70.0;
                if ("Tier 3".equals(tier))
                    return 78.0;
                return 999.0;
        }
    }

    private double calculateMatchScore(MatchingRequest.StudentInfoDTO student, double required, String tier,
            String category) {
        if (required >= 900.0)
            return 0.0;
        if (student.getGpa() == null)
            return 0.0;

        double gpa = convertGpaToPercentage(student.getGpa(), student.getGpaScale());
        double score = 0.0;

        // 1. 基础均分分
        if (gpa >= required + 5)
            score += 95.0;
        else if (gpa >= required + 2)
            score += 85.0;
        else if (gpa >= required)
            score += 75.0; // 达标
        else if (gpa >= required - 2)
            score += 60.0; // 冲刺
        else
            score += 20.0;

        // 2. 院校背景加分
        if ("Tier 1".equals(tier))
            score += 5.0; // 双一流优势

        // 3. 计算机专业对Tier 3直接拒信已在 requiredGpa 处理，此处无需额外

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
        if (score >= 90)
            return "保底";
        if (score >= 75)
            return "稳妥";
        if (score >= 60)
            return "冲刺";
        return "不建议";
    }

    private double calculateAdmissionProbability(double score) {
        if (score >= 90)
            return 0.98;
        if (score >= 75)
            return 0.85;
        if (score >= 60)
            return 0.50;
        return 0.10;
    }

    private double calculateRecommendationIndex(double score) {
        return Math.min(1.0, score / 100.0);
    }

    private String generateRiskAnalysis(MatchingRequest.StudentInfoDTO student, String tier, String category,
            double required, double score) {
        StringBuilder sb = new StringBuilder();
        sb.append("【利物浦大学匹配分析】\n");

        sb.append("1. 院校背景：您的院校属于 ").append(tier).append(" (Tier 1=双一流, Tier 2=双非/转设, Tier 3=独立学院)。\n");
        sb.append("2. 申请专业：").append(category).append(" 类。\n");

        if (required >= 900.0) {
            sb.append("风险提示：该类专业（如计算机）不接受 Tier 3 院校申请，建议更换专业或学校。\n");
        } else {
            sb.append("3. 均分要求：最低 ").append((int) required).append("%。\n");
            double gpaPercent = convertGpaToPercentage(student.getGpa(), student.getGpaScale());
            String gpaText = student.getGpa() == null ? "未填写"
                    : (student.getGpaScale() == null || student.getGpaScale().trim().isEmpty()
                            ? String.format("%.1f", student.getGpa())
                            : String.format("%.2f/%s", student.getGpa(), student.getGpaScale().trim()));
            sb.append("4. 您的均分：").append(String.format("%.1f", gpaPercent)).append("%（").append(gpaText)
                    .append("）。\n");

            if (gpaPercent >= required) {
                sb.append("结果：成绩达标，利物浦大学录取相对友好，希望很大。");
            } else if (gpaPercent >= required - 2) {
                sb.append("结果：成绩略有差距，利物浦大学对此有一定Argue空间，可作为冲刺。");
            } else {
                sb.append("结果：成绩差距较大，建议慎重。");
            }
        }
        return sb.toString();
    }
}
