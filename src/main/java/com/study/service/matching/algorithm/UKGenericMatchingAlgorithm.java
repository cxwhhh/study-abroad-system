package com.study.service.matching.algorithm;

import com.study.service.matching.MatchingDataService;
import com.study.service.matching.MatchingResult;
import com.study.service.matching.dto.MatchingRequest;
import com.study.service.matching.rules.UkSchoolRuleEngineService;
import com.study.service.schools.School;
import com.study.service.major.Major;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@SuppressWarnings("unused")
public class UKGenericMatchingAlgorithm implements SchoolMatchingStrategy {

    @Autowired
    private MatchingDataService dataService;

    @Autowired(required = false)
    private UkSchoolRuleEngineService ukRuleEngineService;

    private static final Set<String> G5_UNIVERSITIES = new HashSet<>(Arrays.asList(
            "University of Cambridge", "University of Oxford", "Imperial College London",
            "University College London", "London School of Economics and Political Science",
            "剑桥大学", "牛津大学", "帝国理工学院", "伦敦大学学院", "伦敦政治经济学院"));

    private static final Set<String> RUSSELL_GROUP = new HashSet<>(Arrays.asList(
            "University of Birmingham", "University of Bristol", "University of Cambridge",
            "Cardiff University", "Durham University", "University of Edinburgh",
            "University of Exeter", "University of Glasgow", "Imperial College London",
            "King's College London", "University of Leeds", "University of Liverpool",
            "London School of Economics and Political Science", "University of Manchester",
            "Newcastle University", "University of Nottingham", "University of Oxford",
            "Queen Mary University of London", "Queen's University Belfast", "University of Sheffield",
            "University of Southampton", "University College London", "University of Warwick",
            "University of York",
            "伯明翰大学", "布里斯托大学", "剑桥大学", "卡迪夫大学", "杜伦大学", "爱丁堡大学",
            "埃克塞特大学", "格拉斯哥大学", "帝国理工学院", "伦敦国王学院", "利兹大学",
            "利物浦大学", "伦敦政治经济学院", "曼彻斯特大学", "纽卡斯尔大学", "诺丁汉大学",
            "牛津大学", "伦敦玛丽女王大学", "贝尔法斯特女王大学", "谢菲尔德大学",
            "南安普顿大学", "伦敦大学学院", "华威大学", "约克大学"));

    @Override
    public String getSchoolName() {
        return "英国通用算法";
    }

    @Override
    public String getRegion() {
        return "英国";
    }

    @Override
    public boolean supports(MatchingRequest request) {
        if (request == null || request.getRegion() == null) {
            return false;
        }
        return "英国".equalsIgnoreCase(request.getRegion().trim());
    }

    @Override
    public List<MatchingResult> computeMatchingResults(MatchingRequest request) {
        List<School> ukSchools = dataService.getSchoolsByRegion("英国");
        List<MatchingResult> results = new ArrayList<>();
        if (ukSchools == null || ukSchools.isEmpty()) {
            return results;
        }
        if (request == null || request.getStudentInfo() == null) {
            return results;
        }
        String targetMajorKeyword = request.getStudentInfo().getTargetMajor();
        for (School school : ukSchools) {
            if (school == null || school.getId() == null) {
                continue;
            }
            if (!isTargetSchoolIncluded(request, school)) {
                continue;
            }
            List<Major> majors = dataService.getMajorsBySchoolId(school.getId());
            List<Major> selectedMajors = selectMajorsForSchool(request, majors, targetMajorKeyword);
            if (selectedMajors == null || selectedMajors.isEmpty()) {
                String fallbackMajorName = (targetMajorKeyword == null || targetMajorKeyword.trim().isEmpty())
                        ? "未配置专业"
                        : targetMajorKeyword.trim();
                RuleDecision decision = resolveRuleDecision(request.getStudentInfo(), school, null, fallbackMajorName);
                double matchScore = calculateMatchScore(request.getStudentInfo(), school, decision, fallbackMajorName);
                MatchingResult r = new MatchingResult();
                r.setUserId(request.getStudentInfo().getUserId());
                r.setSchoolId(school.getId());
                r.setSchoolName(school.getName());
                r.setMajorName(fallbackMajorName);
                r.setMatchScore(matchScore);
                r.setMatchLevel(determineMatchLevel(matchScore, school));
                String reason = generateMatchReason(request.getStudentInfo(), school, fallbackMajorName, decision,
                        matchScore);
                r.setRiskAnalysis(reason);
                r.setMatchReason(reason);
                r.setAdmissionProbability(decision.reject ? 0.01 : calculateAdmissionProbability(matchScore));
                r.setRecommendationIndex(calculateRecommendationIndex(matchScore, school));
                r.setRegion("英国");
                r.setAlgorithmStrategy("UK_GENERIC_MATCHING_ALGORITHM_SCHOOL");
                results.add(r);
                continue;
            }
            for (Major major : selectedMajors) {
                RuleDecision decision = resolveRuleDecision(request.getStudentInfo(), school, major, major.getName());
                double matchScore = calculateMatchScore(request.getStudentInfo(), school, decision, major.getName());
                MatchingResult r = new MatchingResult();
                r.setUserId(request.getStudentInfo().getUserId());
                r.setSchoolId(school.getId());
                r.setSchoolName(school.getName());
                r.setMajorName(major.getName());
                r.setMatchScore(matchScore);
                r.setMatchLevel(determineMatchLevel(matchScore, school));
                String reason = generateMatchReason(request.getStudentInfo(), school, major.getName(), decision,
                        matchScore);
                r.setRiskAnalysis(reason);
                r.setMatchReason(reason);
                r.setAdmissionProbability(decision.reject ? 0.01 : calculateAdmissionProbability(matchScore));
                r.setRecommendationIndex(calculateRecommendationIndex(matchScore, school));
                r.setRegion("英国");
                r.setAlgorithmStrategy("UK_GENERIC_MATCHING_ALGORITHM_MAJOR");
                results.add(r);
            }
        }
        results.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));
        return results;
    }

    private boolean isTargetSchoolIncluded(MatchingRequest request, School school) {
        if (request.getTargetSchools() == null || request.getTargetSchools().isEmpty()) {
            return true;
        }
        String cn = school.getName() == null ? "" : school.getName().trim();
        String en = school.getEnglishName() == null ? "" : school.getEnglishName().trim();
        for (String t : request.getTargetSchools()) {
            if (t == null || t.trim().isEmpty()) {
                continue;
            }
            String target = t.trim();
            if (target.equalsIgnoreCase(cn) || target.equalsIgnoreCase(en)) {
                return true;
            }
            if (!cn.isEmpty() && cn.contains(target)) {
                return true;
            }
            if (!en.isEmpty() && en.toLowerCase().contains(target.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private List<Major> selectMajorsForSchool(MatchingRequest request, List<Major> majors, String targetMajorKeyword) {
        if (majors == null || majors.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> tokens = new LinkedHashSet<>();
        if (request != null && request.getTargetMajors() != null && !request.getTargetMajors().isEmpty()) {
            for (String t : request.getTargetMajors()) {
                tokens.addAll(splitTokens(t));
            }
        }
        tokens.addAll(splitTokens(targetMajorKeyword));

        if (!tokens.isEmpty()) {
            List<Major> preferred = new ArrayList<>();
            for (Major m : majors) {
                if (m == null) {
                    continue;
                }
                String name = m.getName() == null ? "" : m.getName().trim().toLowerCase(Locale.ROOT);
                String en = m.getEnglishName() == null ? "" : m.getEnglishName().trim().toLowerCase(Locale.ROOT);
                for (String t : tokens) {
                    if ((!name.isEmpty() && (name.equals(t) || name.contains(t) || t.contains(name)))
                            || (!en.isEmpty() && (en.equals(t) || en.contains(t) || t.contains(en)))) {
                        preferred.add(m);
                        break;
                    }
                }
            }
            if (!preferred.isEmpty()) {
                return preferred;
            }
        }

        return majors.size() > 10 ? majors.subList(0, 10) : majors;
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

    private String generateMatchReason(MatchingRequest.StudentInfoDTO studentInfo, School school, String majorName,
            RuleDecision decision, double matchScore) {
        Double gpa = studentInfo == null ? null : studentInfo.getGpa();
        String gpaScale = studentInfo == null ? null : studentInfo.getGpaScale();
        String gpaText = gpa == null ? "未填写"
                : (("100".equals(gpaScale) ? String.format("%.1f/100", gpa)
                        : String.format("%.2f/%s", gpa, gpaScale == null ? "?" : gpaScale)));

        StringBuilder sb = new StringBuilder();
        sb.append("当前为英国学校兜底匹配逻辑（已接入规则库按学校区分门槛）。")
                .append(" 学生均分：").append(gpaText)
                .append("；专业：").append(majorName == null ? "" : majorName);

        if (decision != null) {
            sb.append("；门槛来源：").append(decision.source == null ? "默认" : decision.source);
            if (decision.groupCode != null && !decision.groupCode.isBlank()) {
                sb.append("；院校组别：").append(decision.groupCode);
            }
            if (decision.degreeReq != null && !decision.degreeReq.isBlank()) {
                sb.append("；学位要求：").append(decision.degreeReq);
            }
            if (decision.majorCategory != null && !decision.majorCategory.isBlank()) {
                sb.append("；专业大类：").append(decision.majorCategory);
            }
            if (decision.reject) {
                sb.append("；规则判定：拒绝");
            } else if (decision.requiredScore != null) {
                sb.append("；均分门槛：").append(String.format(Locale.ROOT, "%.1f", decision.requiredScore)).append("%");
            }
        }

        sb.append("；综合得分：").append(String.format(Locale.ROOT, "%.1f", matchScore)).append("。");
        return sb.toString();
    }

    private String determineStudentMajorType(MatchingRequest.StudentInfoDTO s) {
        String p = s.getTargetMajor();
        String u = s.getUndergraduateMajor();
        String src = (p != null && !p.trim().isEmpty()) ? p : u;
        return determineMajorType(src);
    }

    private String determineMajorType(String major) {
        if (major == null || major.trim().isEmpty())
            return "other_masters";
        String m = major.toLowerCase();
        if (m.contains("business") || m.contains("economics") || m.contains("management") || m.contains("finance")
                || m.contains("accounting") || m.contains("marketing") || m.contains("商") || m.contains("经济")
                || m.contains("管理") || m.contains("金融") || m.contains("会计") || m.contains("市场")) {
            return "business_economics_non_quantitative";
        }
        return "other_masters";
    }

    private double calculateMatchScore(MatchingRequest.StudentInfoDTO studentInfo, School school,
            RuleDecision decision, String currentMajorName) {
        double total = 0.0;
        double gpaScore = calculateGpaScore(studentInfo, decision);
        total += gpaScore * 0.55;
        double majorScore = calculateMajorScore(studentInfo.getTargetMajor(), currentMajorName);
        total += majorScore * 0.20;
        double reputationScore = calculateReputationScore(school);
        total += reputationScore * 0.15;
        double score = Math.min(100.0, Math.max(0.0, total));
        if (decision != null && decision.reject) {
            score = Math.min(score, 20.0);
        }
        return score;
    }

    private double calculateGpaScore(MatchingRequest.StudentInfoDTO s, RuleDecision decision) {
        if (s == null || s.getGpa() == null) {
            return 40.0;
        }
        if (decision != null && decision.reject) {
            return 0.0;
        }
        double g = convertGpaToPercentage(s.getGpa(), s.getGpaScale());
        double need = decision != null && decision.requiredScore != null ? decision.requiredScore
                : defaultRequiredScoreByReputation();
        if (g >= need + 10) {
            return 95.0;
        } else if (g >= need + 5) {
            return 85.0;
        } else if (g >= need) {
            return 75.0;
        } else if (g >= need - 5) {
            return 60.0;
        } else if (g >= need - 10) {
            return 40.0;
        } else {
            return 20.0;
        }
    }

    private double convertGpaToPercentage(Double g, String scale) {
        if (g == null)
            return 0.0;
        if ("100".equals(scale))
            return g;
        if ("4".equals(scale) || "4.0".equals(scale))
            return (g / 4.0) * 100.0;
        if ("5".equals(scale) || "5.0".equals(scale))
            return (g / 5.0) * 100.0;
        return g;
    }

    private double calculateMajorScore(String t, String c) {
        if (t == null || t.trim().isEmpty())
            return 70.0;
        String tl = t.toLowerCase();
        String cl = c == null ? "" : c.toLowerCase();
        if (cl.contains(tl))
            return 92.0;
        String tt = determineMajorType(t);
        String ct = determineMajorType(c);
        if (tt.equals(ct))
            return 82.0;
        return 60.0;
    }

    private double calculateReputationScore(School s) {
        if (G5_UNIVERSITIES.contains(s.getName()))
            return 95.0;
        else if (RUSSELL_GROUP.contains(s.getName()))
            return 85.0;
        Integer qs = s.getQsRanking();
        if (qs != null && qs > 0) {
            if (qs <= 50)
                return 80.0;
            else if (qs <= 100)
                return 70.0;
            else if (qs <= 200)
                return 60.0;
            else
                return 50.0;
        }
        return 60.0;
    }

    private double calculateDifficultyScore(School s, String group) {
        double base = 70.0;
        if (G5_UNIVERSITIES.contains(s.getName()))
            base = 40.0;
        else if (RUSSELL_GROUP.contains(s.getName()))
            base = 60.0;
        else
            base = 80.0;
        switch (group) {
            case "Group 1":
                base += 20.0;
                break;
            case "Group 2":
                base += 15.0;
                break;
            case "Group 3":
                base += 10.0;
                break;
            case "Group 4":
                base += 5.0;
                break;
        }
        return Math.min(95.0, base);
    }

    private String determineMatchLevel(double score, School s) {
        double t = 60.0;
        return score >= t ? "符合要求" : "不符合要求";
    }

    private double calculateAdmissionProbability(double s) {
        if (s >= 90)
            return 0.9;
        else if (s >= 80)
            return 0.75;
        else if (s >= 70)
            return 0.6;
        else if (s >= 60)
            return 0.4;
        else if (s >= 50)
            return 0.25;
        else
            return 0.1;
    }

    private double calculateRecommendationIndex(double score, School school) {
        double base = score / 100.0;
        if (G5_UNIVERSITIES.contains(school.getName()))
            base += 0.1;
        else if (RUSSELL_GROUP.contains(school.getName()))
            base += 0.05;
        return Math.min(1.0, base);
    }

    private static final class RuleDecision {
        private final String source;
        private final String groupCode;
        private final String degreeReq;
        private final String majorCategory;
        private final Double requiredScore;
        private final boolean reject;

        private RuleDecision(String source, String groupCode, String degreeReq, String majorCategory,
                Double requiredScore, boolean reject) {
            this.source = source;
            this.groupCode = groupCode;
            this.degreeReq = degreeReq;
            this.majorCategory = majorCategory;
            this.requiredScore = requiredScore;
            this.reject = reject;
        }
    }

    private RuleDecision resolveRuleDecision(MatchingRequest.StudentInfoDTO studentInfo, School school, Major major,
            String majorName) {
        if (studentInfo == null || school == null || school.getId() == null) {
            return new RuleDecision("DEFAULT", null, null, null, defaultRequiredScoreByReputation(), false);
        }

        String undergrad = safeTrim(studentInfo.getUndergraduateSchool());
        if (ukRuleEngineService == null || undergrad.isEmpty()) {
            return new RuleDecision("DEFAULT", null, null, null, defaultRequiredScoreByReputation(), false);
        }

        String groupCode = ukRuleEngineService.resolveGroupCodeOrNull(school.getId(), undergrad);
        if (groupCode == null || groupCode.trim().isEmpty()) {
            return new RuleDecision("DEFAULT", null, null, null, defaultRequiredScoreByReputation(), false);
        }

        String degreeReq = resolveDegreeReqFromTexts(
                major == null ? null : major.getAdmissionRequirements(),
                school.getEntryRequirement());
        String majorCategory = resolveMajorCategory(studentInfo, major, majorName);
        UkSchoolRuleEngineService.RequirementDecision decision = ukRuleEngineService.resolveRequirementOrNull(
                school.getId(),
                groupCode,
                degreeReq,
                majorCategory);
        if (decision == null) {
            return new RuleDecision("UK_RULE_ENGINE", groupCode, degreeReq, majorCategory,
                    defaultRequiredScoreByReputation(), false);
        }
        if (decision.isReject()) {
            return new RuleDecision("UK_RULE_ENGINE", groupCode, degreeReq, majorCategory, null, true);
        }
        Double required = decision.getRequiredScore() == null ? defaultRequiredScoreByReputation()
                : decision.getRequiredScore();
        return new RuleDecision("UK_RULE_ENGINE", groupCode, degreeReq, majorCategory, required, false);
    }

    private double defaultRequiredScoreByReputation() {
        return 75.0;
    }

    private String resolveDegreeReqFromTexts(String majorReqText, String schoolReqText) {
        String text = null;
        if (majorReqText != null && !majorReqText.trim().isEmpty()) {
            text = majorReqText;
        } else if (schoolReqText != null && !schoolReqText.trim().isEmpty()) {
            text = schoolReqText;
        }
        if (text == null) {
            return null;
        }
        String t = text.toLowerCase(Locale.ROOT);
        if (t.contains("2:1") || t.contains("2.1") || t.contains("upper second") || t.contains("upper-second")) {
            return "2:1";
        }
        if (t.contains("2:2") || t.contains("2.2") || t.contains("lower second") || t.contains("lower-second")) {
            return "2:2";
        }
        return null;
    }

    private String resolveMajorCategory(MatchingRequest.StudentInfoDTO studentInfo, Major major, String majorName) {
        StringBuilder sb = new StringBuilder();
        if (majorName != null && !majorName.trim().isEmpty()) {
            sb.append(majorName).append(" ");
        }
        if (major != null && major.getEnglishName() != null && !major.getEnglishName().trim().isEmpty()) {
            sb.append(major.getEnglishName()).append(" ");
        }
        if (studentInfo != null && studentInfo.getTargetMajor() != null
                && !studentInfo.getTargetMajor().trim().isEmpty()) {
            sb.append(studentInfo.getTargetMajor()).append(" ");
        }
        String text = sb.toString().trim().toLowerCase(Locale.ROOT);
        if (text.isEmpty()) {
            return null;
        }
        if (containsAny(text,
                "computer", "computing", "software", "data", "ai", "artificial intelligence",
                "engineering", "electronic", "electrical", "mechanical", "civil", "chemical",
                "aerospace", "robot", "information", "communication",
                "计算机", "软件", "数据", "人工智能", "工程", "电子", "电气", "机械", "土木", "化学", "航天", "机器人", "信息", "通信")) {
            return "ECS_ENGINEERING";
        }
        return null;
    }

    private boolean containsAny(String text, String... keywords) {
        if (text == null || text.isEmpty() || keywords == null || keywords.length == 0) {
            return false;
        }
        for (String k : keywords) {
            if (k != null && !k.isEmpty() && text.contains(k)) {
                return true;
            }
        }
        return false;
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
