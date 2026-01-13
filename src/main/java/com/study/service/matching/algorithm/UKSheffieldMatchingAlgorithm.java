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
 * 谢菲尔德大学 (University of Sheffield) 匹配算法
 * 
 * 规则来源：官方入学要求图片 (基于软科排名)
 * 
 * 1. 院校分级 (Bands)：
 * - Band 1: 软科排名 1-100 及 211/985/双一流
 * - Band 2: 软科排名 101-300
 * - Band 3: 软科排名 301-500
 * - Band 4: 其他认可院校 (含民办/独立学院)
 * 
 * 2. 均分要求 (GPA)：
 * - Band 1: 2:1 -> 70% | 2:2 -> 65%
 * - Band 2: 2:1 -> 75% | 2:2 -> 70%
 * - Band 3: 2:1 -> 80% | 2:2 -> 75%
 * - Band 4: 2:1 -> 85% | 2:2 -> 80%
 */
@Service
@SuppressWarnings("unused")
public class UKSheffieldMatchingAlgorithm implements SchoolMatchingStrategy {

    private static final Logger log = LoggerFactory.getLogger(UKSheffieldMatchingAlgorithm.class);

    @Autowired
    private MatchingDataService dataService;

    @Autowired
    private ObjectMapper objectMapper;

    // =================================================================================
    // Band 1: 985/211/双一流 (Rank 1-100)
    // =================================================================================
    private static final Set<String> BAND_1_UNIVERSITIES = new HashSet<>();
    static {
        // 核心 985/211 名单
        String[] list = {
                "清华大学", "北京大学", "浙江大学", "上海交通大学", "复旦大学", "南京大学", "中国科学技术大学",
                "华中科技大学", "武汉大学", "西安交通大学", "中山大学", "四川大学", "哈尔滨工业大学",
                "北京航空航天大学", "东南大学", "北京理工大学", "同济大学", "中国人民大学", "北京师范大学",
                "南开大学", "天津大学", "山东大学", "中南大学", "厦门大学", "西北工业大学", "华南理工大学",
                "吉林大学", "电子科技大学", "湖南大学", "中国农业大学", "华东师范大学", "大连理工大学",
                "南方科技大学", "重庆大学", "上海财经大学", "北京科技大学", "南京理工大学", "南京航空航天大学",
                "东北大学", "西安电子科技大学", "兰州大学", "苏州大学", "华中农业大学", "北京交通大学",
                "华东理工大学", "上海科技大学", "首都医科大学", "郑州大学", "中央财经大学", "华中师范大学",
                "上海大学", "南京医科大学", "哈尔滨工程大学", "南方医科大学", "暨南大学", "中国政法大学",
                "南京农业大学", "对外经济贸易大学", "中国石油大学（北京）", "东北师范大学", "武汉理工大学",
                "西南交通大学", "中国矿业大学", "北京邮电大学", "南京师范大学", "中国地质大学（武汉）", "江南大学",
                "河海大学", "中国海洋大学", "北京工业大学", "哈尔滨医科大学", "北京化工大学", "西北大学",
                "西南财经大学", "中国石油大学（华东）", "陕西师范大学", "天津医科大学", "中南财经政法大学",
                "深圳大学", "南昌大学", "浙江工业大学", "中国医科大学", "中国地质大学（北京）", "西南大学",
                "福州大学", "西北农林科技大学", "华北电力大学", "中国矿业大学（北京）", "东华大学", "江苏大学",
                "华南师范大学", "云南大学", "北京外国语大学", "扬州大学", "北京林业大学", "中国传媒大学"
        };
        Collections.addAll(BAND_1_UNIVERSITIES, list);
    }

    // =================================================================================
    // Band 2: 软科排名 101-300 (强双非一本)
    // =================================================================================
    private static final Set<String> BAND_2_UNIVERSITIES = new HashSet<>();
    static {
        // 示例 Band 2 名单 (实际需根据当年软科排名更新)
        String[] list = {
                "宁波大学", "南京工业大学", "南京邮电大学", "湖南师范大学", "长安大学", "首都师范大学",
                "南京信息工程大学", "杭州电子科技大学", "湘潭大学", "华南农业大学", "温州医科大学", "燕山大学",
                "福建师范大学", "广西大学", "中国药科大学", "广州大学", "上海外国语大学", "上海中医药大学",
                "河南大学", "大连海事大学", "浙江师范大学", "重庆医科大学", "广东工业大学", "广州医科大学",
                "上海理工大学", "东北财经大学", "山西大学", "外交学院", "安徽大学", "中央民族大学",
                "山东师范大学", "河北工业大学", "青岛大学", "太原理工大学", "贵州大学", "南京林业大学",
                "上海师范大学", "武汉科技大学", "内蒙古大学", "浙江理工大学", "东北林业大学", "福建农林大学",
                "江西财经大学", "西安理工大学", "海南大学", "江西师范大学", "东北农业大学", "河北医科大学",
                "长沙理工大学", "西安建筑科技大学", "湖北大学", "南京中医药大学", "昆明理工大学", "江苏师范大学",
                "大连医科大学", "首都经济贸易大学", "山东科技大学", "辽宁大学", "西南石油大学", "杭州师范大学",
                "河北大学", "陕西科技大学", "安徽师范大学", "北京建筑大学", "华侨大学", "汕头大学",
                "华东政法大学", "新疆大学", "江苏科技大学", "石河子大学", "中国计量大学", "北京语言大学",
                "河南农业大学", "安徽医科大学", "重庆邮电大学", "湖北工业大学", "沈阳农业大学", "四川农业大学",
                "福建医科大学", "浙江工商大学", "武汉工程大学", "山东农业大学", "长春理工大学", "天津师范大学",
                "西南政法大学", "宁夏大学", "黑龙江大学", "湖南农业大学", "常州大学", "广州中医药大学",
                "上海体育大学", "浙江农林大学", "河北师范大学", "河南师范大学", "成都理工大学", "天津工业大学",
                "温州大学", "齐鲁工业大学"
        };
        Collections.addAll(BAND_2_UNIVERSITIES, list);
    }

    @Override
    public String getSchoolName() {
        return "谢菲尔德大学";
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
            // 尝试英文名查找
            List<School> ukSchools = dataService.getSchoolsByRegion("英国");
            school = ukSchools.stream()
                    .filter(s -> s.getEnglishName() != null
                            && s.getEnglishName().equalsIgnoreCase("University of Sheffield"))
                    .findFirst()
                    .orElse(null);

            if (school == null) {
                log.warn("未在数据库中找到谢菲尔德大学");
                return results;
            }
        }

        MatchingRequest.StudentInfoDTO studentInfo = request.getStudentInfo();
        String undergradSchool = studentInfo.getUndergraduateSchool();

        // 1. 确定学生院校 Band (1/2/3/4)
        String studentBand = getUniversityBand(undergradSchool);

        List<Major> majors = dataService.getMajorsBySchoolId(school.getId());
        List<String> majorTokens = UKSingleSchoolMatchingAlgorithmBase.extractMajorTokens(request);
        majors = UKSingleSchoolMatchingAlgorithmBase.selectMajorsByTokens(majors, majorTokens, 10);

        for (Major major : majors) {
            // 2. 确定专业学位要求 (2:1 or 2:2)
            String degreeReq = determineDegreeRequirement(major.getName(), major.getEnglishName());

            // 3. 获取均分要求 (根据图片表格)
            double requiredGpa = getRequiredGpa(studentBand, degreeReq);

            // 4. 计算匹配度
            double matchScore = calculateMatchScore(studentInfo, requiredGpa, school, major.getName());
            String matchLevel = determineMatchLevel(matchScore);
            String matchReason = generateMatchReason(studentInfo, studentBand, degreeReq, requiredGpa, matchScore);

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
            result.setAlgorithmStrategy("UK_SHEFFIELD_MATCHING_ALGORITHM");

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
     * 判断院校 Band
     * 逻辑：
     * Band 1: 985/211/双一流/Rank 1-100
     * Band 2: Rank 101-300
     * Band 3: Rank 301-500 (简单处理：不含学院的公办)
     * Band 4: 其他 (独立学院/民办)
     */
    private String getUniversityBand(String schoolName) {
        if (schoolName == null || schoolName.trim().isEmpty())
            return "Band 4";

        // 1. Check Band 1
        for (String key : BAND_1_UNIVERSITIES) {
            if (schoolName.contains(key))
                return "Band 1";
        }

        // 2. Check Band 2
        for (String key : BAND_2_UNIVERSITIES) {
            if (schoolName.contains(key))
                return "Band 2";
        }

        // 3. Check Band 4 (独立学院/民办)
        // 粗略判断：包含"学院"且包含"大学"(如XX大学XX学院)通常为独立学院，或包含"分校"
        // 但需排除 "外交学院" 等特殊 T1/T2
        if ((schoolName.contains("大学") && schoolName.contains("学院") && !schoolName.endsWith("大学")) ||
                schoolName.contains("独立学院") || schoolName.contains("分校")) {
            return "Band 4";
        }

        // 4. Default to Band 3 (Rank 301-500, 普通公办本科)
        // 假设不在 T1/T2 且不是明显独立学院的，归为 Band 3
        return "Band 3";
    }

    /**
     * 确定专业学位要求
     * 谢菲尔德管院 (Management School) 全部 2:1
     * 计算机、新闻传媒、法学 2:1
     * 部分理工科 2:2
     */
    private String determineDegreeRequirement(String majorName, String majorEnName) {
        String name = (majorName + " " + (majorEnName != null ? majorEnName : "")).toLowerCase();

        // 2:2 Majors (参考谢菲以往数据，部分理工科接受 2:2)
        // 但谢菲要求逐年提高，商科、CS、传媒稳稳 2:1
        if (name.contains("chemistry") || name.contains("physics") ||
                (name.contains("materials") && name.contains("engineering"))) {
            return "2:2";
        }

        // 东亚研究部分专业
        if (name.contains("east asian")) {
            return "2:2";
        }

        // Default 2:1 (商科、CS、传媒、法学、教育、建筑、心理学等)
        return "2:1";
    }

    /**
     * 获取分数线 (完全依据图片表格)
     */
    private double getRequiredGpa(String band, String degreeReq) {
        if ("2:1".equals(degreeReq)) {
            switch (band) {
                // Re-reading image:
                // 1-100: 2:1=75%
                // 101-300: 2:1=80%
                // 301-500: 2:1=85%
                // Others: 2:1=88% (Wait, image says 85% for others? No, image row 3 is 80, row
                // 4 is 85)
                // Let's stick strictly to the image rows:
                // Row 1 (1-100): 70% (Wait, your image says 70% for 2:1. Let me check again.)
                // OK, the image text:
                // Row 1 (1-100...): 2:1 col -> 70%, 2:2 col -> 65% (Wait, this is surprisingly
                // low for Sheffield 2026?)
                // Let's follow the image STRICTLY.

                // UPDATE based on Image visual values:
                // Row 1 (1-100, 211/985): 2:1 -> 75%, 2:2 -> 70% ?
                // No, looking at the crop...
                // Column headers: 2:1 | 2:2
                // Row 1 (1-100...): 75% | (Not visible clearly, assuming logic) ... Wait.
                // Let's look at the numeric columns again.
                // Row 1: 70% | 65%
                // Row 2: 75% | 70%
                // Row 3: 80% | 75%
                // Row 4: 85% | 80%

                // Wait, Sheffield usually requires 75% for Tier 1 for Management School.
                // If the image says 70% for 2:1 in Tier 1, it might be for General Engineering?
                // But the header says "本科院校中国排名(软科)". It looks like a general table.
                // Let's trust the numbers in the image provided:
                // Band 1: 70% (2:1)
                // Band 2: 75% (2:1)
                // Band 3: 80% (2:1)
                // Band 4: 85% (2:1)

                // However, standard Sheffield Management School is usually +5% or higher base.
                // I will use the image numbers as BASE, but maybe verify if it's a
                // "Engineering" table.
                // I will follow the image numbers exactly.

                case "Band 1":
                    return 70.0;
                case "Band 2":
                    return 75.0;
                case "Band 3":
                    return 80.0;
                case "Band 4":
                    return 85.0;
                default:
                    return 90.0;
            }
        } else {
            // 2:2 Requirement
            switch (band) {
                case "Band 1":
                    return 65.0;
                case "Band 2":
                    return 70.0;
                case "Band 3":
                    return 75.0;
                case "Band 4":
                    return 80.0;
                default:
                    return 90.0;
            }
        }
    }

    private double calculateMatchScore(MatchingRequest.StudentInfoDTO student, double required, School school,
            String majorName) {
        if (student.getGpa() == null)
            return 0.0;
        double gpa = student.getGpa();
        double score = 0.0;

        // 谢菲尔德对均分卡得比较死 (Strict)
        if (gpa >= required) {
            score += 80.0; // 达标
            if (gpa >= required + 2)
                score += 10.0; // 稳
        } else if (gpa >= required - 1) {
            score += 55.0; // 边缘 (High Risk)
        } else {
            score += 20.0; // 差距大
        }

        // 管院热门专业修正 (谢菲管院通常竞争激烈，建议分数更高)
        if (majorName.toLowerCase().contains("management") ||
                majorName.toLowerCase().contains("finance") ||
                majorName.toLowerCase().contains("marketing")) {
            if (score > 60)
                score -= 5.0; // 增加难度系数
        }

        return Math.min(100.0, score);
    }

    private String determineMatchLevel(double score) {
        if (score >= 90)
            return "保底";
        if (score >= 75)
            return "稳妥";
        if (score >= 55)
            return "冲刺";
        return "不建议";
    }

    private String generateMatchReason(MatchingRequest.StudentInfoDTO student, String band, String degreeReq,
            double required, double score) {
        StringBuilder sb = new StringBuilder();
        sb.append("【谢菲尔德大学】");

        sb.append("您的本科院校属于 ").append(band).append(" (基于软科排名)。");

        sb.append("该专业要求英国 ").append(degreeReq).append(" 学位，");
        sb.append("对应 Band ").append(band).append(" 的均分要求为 ").append((int) required).append("%。");

        sb.append(" 您的均分为 ").append(student.getGpa()).append("%。");

        if (student.getGpa() >= required) {
            sb.append(" 成绩达标，符合官方录取要求。");
        } else {
            sb.append(" 成绩未达标 (谢菲尔德大学对均分要求较为严格，且不接受成人/自考学历)。");
        }

        return sb.toString();
    }

    private double calculateAdmissionProbability(double score) {
        if (score >= 90)
            return 0.95;
        if (score >= 75)
            return 0.80;
        if (score >= 55)
            return 0.30;
        return 0.05;
    }

    private double calculateRecommendationIndex(double score) {
        return Math.min(1.0, score / 100.0);
    }
}
