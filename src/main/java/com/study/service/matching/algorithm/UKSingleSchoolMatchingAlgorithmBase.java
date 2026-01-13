package com.study.service.matching.algorithm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.service.matching.MatchingDataService;
import com.study.service.matching.MatchingResult;
import com.study.service.matching.dto.MatchingRequest;
import com.study.service.matching.rules.UndergradSchool;
import com.study.service.matching.rules.UndergradSchoolMapper;
import com.study.service.matching.rules.UkSchoolRuleEngineService;
import com.study.service.major.Major;
import com.study.service.schools.School;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class UKSingleSchoolMatchingAlgorithmBase implements SchoolMatchingStrategy {

    @Autowired
    protected MatchingDataService dataService;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired(required = false)
    protected UkSchoolRuleEngineService ukRuleEngineService;

    @Autowired(required = false)
    protected UndergradSchoolMapper undergradSchoolMapper;

    protected abstract List<String> getSchoolAliases();

    protected abstract String getAlgorithmStrategy();

    protected Double schoolFallbackRequiredGpaPercent() {
        return null;
    }

    protected boolean preferSchoolGroupRulesOverText() {
        return false;
    }

    protected GroupRuleDecision schoolFallbackGroupDecision(
            MatchingRequest request,
            School school,
            Major major,
            String majorName,
            String majorReqText,
            String schoolReqText) {
        return null;
    }

    protected RequirementProfile buildRequirementProfile(MatchingRequest request, School school, Major major,
            String majorName) {
        String majorReqText = major == null ? null : major.getAdmissionRequirements();
        String schoolReqText = school == null ? null : school.getEntryRequirement();

        Double majorPercent = parseRequiredGpaPercent(majorReqText);
        Double schoolPercent = parseRequiredGpaPercent(schoolReqText);
        Double requiredGpaPercent = majorPercent != null ? majorPercent : schoolPercent;

        String requirementSource;
        if (majorPercent != null) {
            requirementSource = "MAJOR_TEXT";
        } else if (schoolPercent != null) {
            requirementSource = "SCHOOL_TEXT";
        } else {
            requirementSource = "UNRESOLVED";
        }
        String ruleGroupCode = null;
        String ruleDegreeReq = null;
        String ruleMajorCategory = null;
        Double ruleRequiredScore = null;
        boolean ruleReject = false;

        MatchingRequest.StudentInfoDTO studentInfo = request == null ? null : request.getStudentInfo();
        if (ukRuleEngineService != null && school != null && school.getId() != null
                && studentInfo != null
                && studentInfo.getUndergraduateSchool() != null
                && !studentInfo.getUndergraduateSchool().trim().isEmpty()) {
            ruleGroupCode = ukRuleEngineService.resolveGroupCodeOrNull(school.getId(),
                    studentInfo.getUndergraduateSchool());
            if (ruleGroupCode != null && !ruleGroupCode.trim().isEmpty()) {
                ruleDegreeReq = resolveDegreeReqFromTexts(majorReqText, schoolReqText);
                ruleMajorCategory = resolveMajorCategory(request, major, majorName);
                UkSchoolRuleEngineService.RequirementDecision decision = ukRuleEngineService.resolveRequirementOrNull(
                        school.getId(),
                        ruleGroupCode,
                        ruleDegreeReq,
                        ruleMajorCategory);
                if (decision != null) {
                    ruleReject = decision.isReject();
                    ruleRequiredScore = decision.getRequiredScore();
                    requirementSource = "UK_RULE_ENGINE";
                    if (!ruleReject && ruleRequiredScore != null) {
                        requiredGpaPercent = ruleRequiredScore;
                    }
                }
            }
        }

        boolean fromRuleEngine = "UK_RULE_ENGINE".equals(requirementSource);
        if (!fromRuleEngine && (preferSchoolGroupRulesOverText() || requiredGpaPercent == null)) {
            GroupRuleDecision groupDecision = schoolFallbackGroupDecision(request, school, major, majorName,
                    majorReqText, schoolReqText);
            if (groupDecision != null) {
                ruleGroupCode = groupDecision.groupCode;
                ruleDegreeReq = groupDecision.degreeReq;
                ruleMajorCategory = groupDecision.majorCategory;
                ruleRequiredScore = groupDecision.requiredGpaPercent;
                ruleReject = groupDecision.reject;
                requirementSource = "SCHOOL_GROUP_RULE";
                if (!ruleReject && ruleRequiredScore != null) {
                    requiredGpaPercent = ruleRequiredScore;
                }
            }
        }

        if (requiredGpaPercent == null) {
            Double fallback = schoolFallbackRequiredGpaPercent();
            if (fallback != null) {
                requiredGpaPercent = fallback;
                requirementSource = "SCHOOL_RULE_FALLBACK";
            } else {
                requiredGpaPercent = defaultRequiredGpaPercentByReputation(school);
                requirementSource = "DEFAULT_REPUTATION";
            }
        }

        Double requiredIelts = null;
        if (major != null && major.getIeltsRequirement() != null) {
            requiredIelts = major.getIeltsRequirement();
        } else {
            Double majorIeltsFromText = parseIeltsRequirementFromText(majorReqText);
            Double schoolIeltsFromField = parseIeltsRequirementFromText(
                    school == null ? null : school.getIeltsRequirement());
            Double schoolIeltsFromText = parseIeltsRequirementFromText(schoolReqText);
            if (majorIeltsFromText != null) {
                requiredIelts = majorIeltsFromText;
            } else if (schoolIeltsFromField != null) {
                requiredIelts = schoolIeltsFromField;
            } else if (schoolIeltsFromText != null) {
                requiredIelts = schoolIeltsFromText;
            }
        }

        Integer requiredToefl = parseIntRequirement(major == null ? null : major.getToeflRequirement());
        if (requiredToefl == null) {
            requiredToefl = parseIntRequirement(school == null ? null : school.getToeflRequirement());
        }
        Integer requiredGre = parseIntRequirement(major == null ? null : major.getGreRequirement());
        if (requiredGre == null) {
            requiredGre = parseIntRequirement(school == null ? null : school.getGreRequirement());
        }
        Integer requiredGmat = parseIntRequirement(major == null ? null : major.getGmatRequirement());
        if (requiredGmat == null) {
            requiredGmat = parseIntRequirement(school == null ? null : school.getGmatRequirement());
        }

        Integer requiredWorkYears = parseWorkYearsRequirement(major == null ? null : major.getWorkExperienceRequired());

        RequirementProfile p = new RequirementProfile();
        p.requiredGpaPercent = requiredGpaPercent;
        p.requirementSource = requirementSource;
        p.ruleGroupCode = ruleGroupCode;
        p.ruleDegreeReq = ruleDegreeReq;
        p.ruleMajorCategory = ruleMajorCategory;
        p.ruleRequiredScore = ruleRequiredScore;
        p.ruleReject = ruleReject;
        p.requiredIelts = requiredIelts;
        p.requiredToefl = requiredToefl;
        p.requiredGre = requiredGre;
        p.requiredGmat = requiredGmat;
        p.requiredWorkYears = requiredWorkYears;
        p.majorRequirementText = majorReqText;
        p.schoolRequirementText = schoolReqText;
        p.majorName = majorName;
        return p;
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
        String region = request.getRegion().trim();
        if (!"英国".equalsIgnoreCase(region) && !"uk".equalsIgnoreCase(region)
                && !"united kingdom".equalsIgnoreCase(region)) {
            return false;
        }
        if (request.getTargetSchools() == null || request.getTargetSchools().isEmpty()) {
            return true;
        }
        List<String> aliases = getSchoolAliases();
        if (aliases == null) {
            aliases = Collections.emptyList();
        }
        for (String t : request.getTargetSchools()) {
            if (t == null || t.trim().isEmpty()) {
                continue;
            }
            String target = t.trim();
            if (matchesAlias(target, getSchoolName())) {
                return true;
            }
            for (String a : aliases) {
                if (matchesAlias(target, a)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<MatchingResult> computeMatchingResults(MatchingRequest request) {
        if (request == null || request.getStudentInfo() == null) {
            return Collections.emptyList();
        }

        School school = resolveSchool();
        if (school == null) {
            return Collections.emptyList();
        }

        List<Major> majors = dataService.getMajorsBySchoolId(school.getId());
        List<Major> selectedMajors = selectMajors(request, majors);
        if (selectedMajors == null || selectedMajors.isEmpty()) {
            MatchingResult single = buildResultForMajor(request, school, null, fallbackMajorName(request), null);
            return single == null ? Collections.emptyList() : Collections.singletonList(single);
        }

        List<MatchingResult> results = new ArrayList<>();
        for (Major major : selectedMajors) {
            if (major == null) {
                continue;
            }
            String majorName = major.getName();
            if (majorName == null || majorName.trim().isEmpty()) {
                majorName = fallbackMajorName(request);
            }
            MatchingResult r = buildResultForMajor(request, school, major, majorName, major.getEnglishName());
            if (r != null) {
                results.add(r);
            }
        }
        results.sort((a, b) -> Double.compare(
                b.getMatchScore() == null ? -1 : b.getMatchScore(),
                a.getMatchScore() == null ? -1 : a.getMatchScore()));
        return results;
    }

    private MatchingResult buildResultForMajor(MatchingRequest request, School school, Major major, String majorName,
            String majorEnglishName) {
        MatchingRequest.StudentInfoDTO studentInfo = request.getStudentInfo();
        RequirementProfile requirements = buildRequirementProfile(request, school, major, majorName);

        Double studentGpaPercent = convertGpaToPercentage(studentInfo.getGpa(), studentInfo.getGpaScale());
        double requiredGpa = requirements.requiredGpaPercent == null ? 75.0 : requirements.requiredGpaPercent;
        double gpaScore = calculateGpaScore(studentGpaPercent, requiredGpa);

        LanguageEval languageEval = evaluateLanguage(studentInfo, requirements);
        double languageScore = languageEval.score;

        TestEval testEval = evaluateStandardizedTest(studentInfo, requirements);
        double testScore = testEval.score;

        double backgroundScore = calculateBackgroundScore(studentInfo, requirements);
        double matchScore = Math.min(100.0,
                Math.max(0.0, gpaScore * 0.60 + languageScore * 0.25 + testScore * 0.10 + backgroundScore * 0.05));

        if (languageEval.hardFail) {
            matchScore = Math.min(matchScore, 59.0);
        }

        if (requirements.ruleReject) {
            matchScore = Math.min(matchScore, 20.0);
        }

        String matchLevel = determineMatchLevel(matchScore);
        String analysis = generateRiskAnalysis(studentInfo, school, requirements, studentGpaPercent, requiredGpa,
                languageEval, testEval, backgroundScore, matchScore);

        MatchingResult result = new MatchingResult();
        result.setUserId(studentInfo.getUserId());
        result.setSchoolId(school.getId());
        result.setSchoolName(school.getName());
        result.setMajorName(majorName);
        result.setMajorEnglishName(majorEnglishName);
        result.setMatchScore(matchScore);
        result.setMatchLevel(matchLevel);
        result.setMatchReason(analysis);
        result.setRiskAnalysis(analysis);
        result.setAdmissionProbability(requirements.ruleReject ? 0.01 : calculateAdmissionProbability(matchScore));
        result.setRecommendationIndex(Math.min(1.0, Math.max(0.0, matchScore / 100.0)));
        result.setRegion("英国");
        result.setAlgorithmStrategy(getAlgorithmStrategy());

        try {
            result.setStudentInfoSnapshot(objectMapper.writeValueAsString(studentInfo));
        } catch (Exception e) {
            result.setStudentInfoSnapshot("{}");
        }

        return result;
    }

    private List<Major> selectMajors(MatchingRequest request, List<Major> majors) {
        if (majors == null || majors.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> targetMajors = request == null ? null : request.getTargetMajors();
        String keyword = request == null || request.getStudentInfo() == null ? null
                : request.getStudentInfo().getTargetMajor();

        Set<String> tokens = new LinkedHashSet<>();
        if (targetMajors != null && !targetMajors.isEmpty()) {
            for (String t : targetMajors) {
                tokens.addAll(splitTokens(t));
            }
        }
        tokens.addAll(splitTokens(keyword));
        expandMajorTokens(tokens);

        int limit = 10;
        if (!tokens.isEmpty()) {
            List<Major> ranked = rankMajorsByTokens(majors, new ArrayList<>(tokens), limit);
            if (!ranked.isEmpty()) {
                return ranked;
            }
        }

        return majors.size() > limit ? majors.subList(0, limit) : majors;
    }

    static List<String> splitTokens(String raw) {
        if (raw == null) {
            return Collections.emptyList();
        }
        String trimmed = normalizeMajorQuery(raw);
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
            if (!v.isEmpty() && !isMajorStopword(v)) {
                out.add(v);
            }
        }
        return out;
    }

    private static String normalizeMajorQuery(String raw) {
        if (raw == null) {
            return "";
        }
        String s = raw.trim().toLowerCase(Locale.ROOT);
        if (s.isEmpty()) {
            return "";
        }
        s = s.replace("（", " ").replace("）", " ").replace("(", " ").replace(")", " ")
                .replace("[", " ").replace("]", " ").replace("【", " ").replace("】", " ");
        for (String w : MAJOR_QUERY_REMOVALS) {
            s = s.replace(w, " ");
        }
        s = s.replaceAll("[,，;；、]+", " ");
        s = s.replaceAll("\\s+", " ").trim();
        return s;
    }

    private static boolean isMajorStopword(String token) {
        return token == null || token.isEmpty() || MAJOR_STOPWORDS.contains(token);
    }

    static void expandMajorTokens(Set<String> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            return;
        }
        List<String> snapshot = new ArrayList<>(tokens);
        for (String t : snapshot) {
            if (t == null || t.isEmpty()) {
                continue;
            }
            if (t.contains("金融") || "finance".equals(t) || "financial".equals(t)) {
                tokens.add("finance");
                tokens.add("financial");
                tokens.add("bank");
                tokens.add("banking");
            }
            if (t.contains("会计") || "accounting".equals(t) || "accountancy".equals(t)) {
                tokens.add("accounting");
                tokens.add("accountancy");
            }
            if (t.contains("经济") || "economics".equals(t) || "economic".equals(t)) {
                tokens.add("economics");
                tokens.add("economic");
            }
            if (t.contains("管理") || "management".equals(t)) {
                tokens.add("management");
                tokens.add("business");
            }
            if (t.contains("市场") || "marketing".equals(t)) {
                tokens.add("marketing");
            }
        }
        tokens.removeIf(UKSingleSchoolMatchingAlgorithmBase::isMajorStopword);
    }

    static List<Major> rankMajorsByTokens(List<Major> majors, List<String> tokens, int limit) {
        if (majors == null || majors.isEmpty() || tokens == null || tokens.isEmpty() || limit <= 0) {
            return Collections.emptyList();
        }
        List<ScoredMajor> scored = new ArrayList<>();
        int idx = 0;
        for (Major m : majors) {
            if (m == null) {
                idx++;
                continue;
            }
            String cn = m.getName() == null ? "" : m.getName().trim().toLowerCase(Locale.ROOT);
            String en = m.getEnglishName() == null ? "" : m.getEnglishName().trim().toLowerCase(Locale.ROOT);
            int score = 0;
            for (String t : tokens) {
                if (t == null || t.isEmpty()) {
                    continue;
                }
                score = Math.max(score, tokenMatchScore(cn, t));
                score = Math.max(score, tokenMatchScore(en, t));
            }
            if (score > 0) {
                scored.add(new ScoredMajor(m, score, idx));
            }
            idx++;
        }
        if (scored.isEmpty()) {
            return Collections.emptyList();
        }
        scored.sort((a, b) -> {
            int cmp = Integer.compare(b.score, a.score);
            if (cmp != 0) {
                return cmp;
            }
            return Integer.compare(a.index, b.index);
        });
        List<Major> out = new ArrayList<>();
        for (ScoredMajor sm : scored) {
            out.add(sm.major);
            if (out.size() >= limit) {
                break;
            }
        }
        return out;
    }

    static List<String> extractMajorTokens(MatchingRequest request) {
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
        expandMajorTokens(tokens);
        return new ArrayList<>(tokens);
    }

    static List<Major> selectMajorsByTokens(List<Major> majors, List<String> tokens, int limit) {
        if (majors == null || majors.isEmpty()) {
            return Collections.emptyList();
        }
        int resolvedLimit = limit <= 0 ? 10 : limit;
        if (tokens != null && !tokens.isEmpty()) {
            List<Major> ranked = rankMajorsByTokens(majors, tokens, resolvedLimit);
            if (!ranked.isEmpty()) {
                return ranked;
            }
        }
        return majors.size() > resolvedLimit ? majors.subList(0, resolvedLimit) : majors;
    }

    private static int tokenMatchScore(String value, String token) {
        if (value == null || token == null) {
            return 0;
        }
        if (value.isEmpty() || token.isEmpty()) {
            return 0;
        }
        if (value.equals(token)) {
            return 1000 + token.length();
        }
        if (value.contains(token)) {
            return 500 + token.length();
        }
        if (token.contains(value) && value.length() >= 3) {
            return 100 + value.length();
        }
        return 0;
    }

    private static final Set<String> MAJOR_STOPWORDS = new HashSet<>();
    private static final List<String> MAJOR_QUERY_REMOVALS = new ArrayList<>();
    static {
        Collections.addAll(MAJOR_STOPWORDS,
                "msc", "ma", "mba", "mres", "mphil", "meng", "bsc", "ba", "beng", "phd",
                "master", "masters", "degree", "programme", "program", "course",
                "硕士", "博士", "研究生", "学位", "课程", "项目", "专业");
        Collections.addAll(MAJOR_QUERY_REMOVALS,
                "msc", "ma", "mba", "mres", "mphil", "meng", "bsc", "ba", "beng", "phd",
                "master", "masters", "degree", "programme", "program", "course",
                "硕士", "博士", "研究生", "学位", "课程", "项目", "专业");
    }

    private static final class ScoredMajor {
        private final Major major;
        private final int score;
        private final int index;

        private ScoredMajor(Major major, int score, int index) {
            this.major = major;
            this.score = score;
            this.index = index;
        }
    }

    private School resolveSchool() {
        School byName = dataService.getSchoolByName(getSchoolName());
        if (byName != null) {
            return byName;
        }
        List<School> ukSchools = dataService.getSchoolsByRegion("英国");
        if (ukSchools == null || ukSchools.isEmpty()) {
            return null;
        }
        List<String> aliases = getSchoolAliases();
        if (aliases == null) {
            aliases = Collections.emptyList();
        }
        for (School s : ukSchools) {
            if (s == null) {
                continue;
            }
            String cn = s.getName();
            String en = s.getEnglishName();
            if (matchesAnyAlias(cn, aliases) || matchesAnyAlias(en, aliases) || matchesAlias(cn, getSchoolName())
                    || matchesAlias(en, getSchoolName())) {
                return s;
            }
        }
        return null;
    }

    private boolean matchesAnyAlias(String value, List<String> aliases) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        for (String a : aliases) {
            if (matchesAlias(value, a)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesAlias(String value, String alias) {
        if (value == null || alias == null) {
            return false;
        }
        String v = value.trim();
        String a = alias.trim();
        if (v.isEmpty() || a.isEmpty()) {
            return false;
        }
        if (v.equalsIgnoreCase(a)) {
            return true;
        }
        String vl = v.toLowerCase(Locale.ROOT);
        String al = a.toLowerCase(Locale.ROOT);
        return vl.contains(al) || al.contains(vl);
    }

    private String fallbackMajorName(MatchingRequest request) {
        String targetMajor = request.getStudentInfo() == null ? null : request.getStudentInfo().getTargetMajor();
        return (targetMajor == null || targetMajor.trim().isEmpty()) ? "未配置专业" : targetMajor.trim();
    }

    @SuppressWarnings("unused")
    protected static class RequirementProfile {
        private Double requiredGpaPercent;
        private String requirementSource;
        private String ruleGroupCode;
        private String ruleDegreeReq;
        private String ruleMajorCategory;
        private Double ruleRequiredScore;
        private boolean ruleReject;
        private Double requiredIelts;
        private Integer requiredToefl;
        private Integer requiredGre;
        private Integer requiredGmat;
        private Integer requiredWorkYears;
        private String majorRequirementText;
        private String schoolRequirementText;
        private String majorName;
    }

    protected static class LanguageEval {
        private final boolean hardFail;
        private final double score;
        private final String summary;

        private LanguageEval(boolean hardFail, double score, String summary) {
            this.hardFail = hardFail;
            this.score = score;
            this.summary = summary;
        }
    }

    protected static class TestEval {
        private final double score;
        private final String summary;

        private TestEval(double score, String summary) {
            this.score = score;
            this.summary = summary;
        }
    }

    private static final Set<String> G5_UNIVERSITIES = new HashSet<>();
    private static final Set<String> RUSSELL_GROUP = new HashSet<>();

    static {
        Collections.addAll(G5_UNIVERSITIES,
                "University of Cambridge", "University of Oxford", "Imperial College London",
                "University College London", "London School of Economics and Political Science",
                "剑桥大学", "牛津大学", "帝国理工学院", "伦敦大学学院", "伦敦政治经济学院");
        Collections.addAll(RUSSELL_GROUP,
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
                "南安普顿大学", "伦敦大学学院", "华威大学", "约克大学");
    }

    private double defaultRequiredGpaPercentByReputation(School school) {
        if (school == null) {
            return 75.0;
        }
        String cn = school.getName();
        String en = school.getEnglishName();
        if ((cn != null && G5_UNIVERSITIES.contains(cn)) || (en != null && G5_UNIVERSITIES.contains(en))) {
            return 85.0;
        }
        if ((cn != null && RUSSELL_GROUP.contains(cn)) || (en != null && RUSSELL_GROUP.contains(en))) {
            return 80.0;
        }
        Integer qs = school.getQsRanking();
        if (qs != null && qs > 0 && qs <= 50) {
            return 82.0;
        }
        return 75.0;
    }

    private static final Pattern PERCENT_PATTERN = Pattern.compile("(\\d{2}(?:\\.\\d+)?)\\s*%");
    private static final Pattern GPA_4_PATTERN = Pattern.compile("gpa\\s*[:=]?\\s*(\\d(?:\\.\\d+)?)\\s*/\\s*4(?:\\.0)?",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern IELTS_PATTERN = Pattern.compile("(?:ielts|雅思)\\s*[:=]?\\s*(\\d(?:\\.\\d+)?)",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern ANY_NUMBER_PATTERN = Pattern.compile("(\\d{2,4})");
    private static final Pattern WORK_YEARS_PATTERN = Pattern.compile("(\\d+)\\s*(?:years?|年)",
            Pattern.CASE_INSENSITIVE);

    private Double parseRequiredGpaPercent(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        String t = text.trim();
        String tl = t.toLowerCase(Locale.ROOT);

        Double classification = parseUkClassificationToPercent(tl);
        if (classification != null) {
            return classification;
        }

        Matcher gpa4 = GPA_4_PATTERN.matcher(tl);
        if (gpa4.find()) {
            Double g = safeParseDouble(gpa4.group(1));
            if (g != null && g >= 0.5 && g <= 4.0) {
                return Math.min(100.0, Math.max(0.0, (g / 4.0) * 100.0));
            }
        }

        Pattern kwPercent = Pattern.compile(
                "(?:minimum|min|at\\s*least|require|要求|需|至少|门槛|均分|average)[\\s\\S]{0,40}?(\\d{2}(?:\\.\\d+)?)\\s*%",
                Pattern.CASE_INSENSITIVE);
        Matcher kw = kwPercent.matcher(t);
        if (kw.find()) {
            Double v = safeParseDouble(kw.group(1));
            if (v != null && v >= 55.0 && v <= 95.0) {
                return v;
            }
        }

        Matcher m = PERCENT_PATTERN.matcher(t);
        Double best = null;
        while (m.find()) {
            Double v = safeParseDouble(m.group(1));
            if (v == null) {
                continue;
            }
            if (v < 55.0 || v > 95.0) {
                continue;
            }
            if (best == null || v > best) {
                best = v;
            }
        }
        return best;
    }

    protected String resolveDegreeReqFromTexts(String majorReqText, String schoolReqText) {
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

    protected String resolveMajorCategory(MatchingRequest request, Major major, String majorName) {
        StringBuilder sb = new StringBuilder();
        if (majorName != null && !majorName.trim().isEmpty()) {
            sb.append(majorName).append(" ");
        }
        if (major != null && major.getEnglishName() != null && !major.getEnglishName().trim().isEmpty()) {
            sb.append(major.getEnglishName()).append(" ");
        }
        MatchingRequest.StudentInfoDTO s = request == null ? null : request.getStudentInfo();
        if (s != null && s.getTargetMajor() != null && !s.getTargetMajor().trim().isEmpty()) {
            sb.append(s.getTargetMajor()).append(" ");
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

    private Double parseUkClassificationToPercent(String textLower) {
        if (textLower == null || textLower.isEmpty()) {
            return null;
        }
        if (textLower.contains("1st") || textLower.contains("first class") || textLower.contains("一等")) {
            return 80.0;
        }
        if (textLower.contains("2:1") || textLower.contains("2.1") || textLower.contains("upper second")
                || textLower.contains("二等一") || textLower.contains("二等一等")) {
            return 75.0;
        }
        if (textLower.contains("2:2") || textLower.contains("2.2") || textLower.contains("lower second")
                || textLower.contains("二等二") || textLower.contains("二等二等")) {
            return 70.0;
        }
        return null;
    }

    private Double parseIeltsRequirementFromText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        Matcher m = IELTS_PATTERN.matcher(text);
        if (m.find()) {
            Double v = safeParseDouble(m.group(1));
            if (v != null && v >= 4.0 && v <= 9.0) {
                return v;
            }
        }
        Pattern p = Pattern.compile("(\\d(?:\\.\\d+)?)\\s*(?:ielts|雅思)", Pattern.CASE_INSENSITIVE);
        Matcher m2 = p.matcher(text);
        if (m2.find()) {
            Double v = safeParseDouble(m2.group(1));
            if (v != null && v >= 4.0 && v <= 9.0) {
                return v;
            }
        }
        return null;
    }

    private Integer parseIntRequirement(String requirementText) {
        if (requirementText == null || requirementText.trim().isEmpty()) {
            return null;
        }
        Matcher m = ANY_NUMBER_PATTERN.matcher(requirementText);
        Integer best = null;
        while (m.find()) {
            Integer v = safeParseInt(m.group(1));
            if (v == null) {
                continue;
            }
            if (v < 50 || v > 360) {
                continue;
            }
            if (best == null || v > best) {
                best = v;
            }
        }
        return best;
    }

    private Integer parseWorkYearsRequirement(String workRequirementText) {
        if (workRequirementText == null || workRequirementText.trim().isEmpty()) {
            return null;
        }
        String t = workRequirementText.trim().toLowerCase(Locale.ROOT);
        Matcher m = WORK_YEARS_PATTERN.matcher(t);
        if (m.find()) {
            Integer v = safeParseInt(m.group(1));
            if (v != null && v >= 0 && v <= 20) {
                return v;
            }
        }
        if (t.contains("required") || t.contains("需要") || t.contains("must") || t.contains("必需") || t.contains("必须")) {
            return 1;
        }
        return null;
    }

    private Double convertGpaToPercentage(Double gpa, String scale) {
        if (gpa == null) {
            return null;
        }
        if (scale == null || scale.trim().isEmpty()) {
            return clamp(gpa, 0.0, 100.0);
        }
        String s = scale.trim();
        if ("100".equals(s)) {
            return clamp(gpa, 0.0, 100.0);
        }
        if ("4".equals(s) || "4.0".equals(s)) {
            return clamp((gpa / 4.0) * 100.0, 0.0, 100.0);
        }
        if ("5".equals(s) || "5.0".equals(s)) {
            return clamp((gpa / 5.0) * 100.0, 0.0, 100.0);
        }
        if ("10".equals(s) || "10.0".equals(s)) {
            return clamp((gpa / 10.0) * 100.0, 0.0, 100.0);
        }
        return clamp(gpa, 0.0, 100.0);
    }

    private double calculateGpaScore(Double studentGpaPercent, double requiredGpaPercent) {
        if (studentGpaPercent == null) {
            return 55.0;
        }
        double g = clamp(studentGpaPercent, 0.0, 100.0);
        double required = clamp(requiredGpaPercent, 0.0, 100.0);
        if (required <= 0.0) {
            return 70.0;
        }
        if (g >= required + 8) {
            return 95.0;
        }
        if (g >= required + 4) {
            return 88.0;
        }
        if (g >= required) {
            return 78.0;
        }
        if (g >= required - 2) {
            return 62.0;
        }
        if (g >= required - 5) {
            return 45.0;
        }
        return 20.0;
    }

    private LanguageEval evaluateLanguage(MatchingRequest.StudentInfoDTO student, RequirementProfile req) {
        if (student == null) {
            return new LanguageEval(false, 60.0, "语言：未填写");
        }
        String test = student.getLanguageTest();
        Double score = student.getLanguageScore();
        if (test == null || test.trim().isEmpty() || score == null) {
            if (req != null && (req.requiredIelts != null || req.requiredToefl != null)) {
                return new LanguageEval(false, 55.0, "语言：未填写，存在语言门槛风险");
            }
            return new LanguageEval(false, 70.0, "语言：未填写");
        }
        String t = test.trim().toLowerCase(Locale.ROOT);
        if (t.contains("ielts") || t.contains("雅思")) {
            Double need = req == null ? null : req.requiredIelts;
            if (need == null) {
                return new LanguageEval(false, 75.0, "语言：雅思 " + score + "（未配置门槛）");
            }
            double diff = score - need;
            if (diff >= 1.0) {
                return new LanguageEval(false, 95.0, "语言：雅思 " + score + " ≥ " + need);
            }
            if (diff >= 0.5) {
                return new LanguageEval(false, 88.0, "语言：雅思 " + score + " ≥ " + need);
            }
            if (diff >= 0.0) {
                return new LanguageEval(false, 78.0, "语言：雅思 " + score + " ≥ " + need);
            }
            if (diff >= -0.5) {
                return new LanguageEval(false, 60.0, "语言：雅思 " + score + " < " + need + "（可作为冲刺风险）");
            }
            return new LanguageEval(true, 20.0, "语言：雅思 " + score + " < " + need + "（硬性不达标）");
        }
        if (t.contains("toefl") || t.contains("托福")) {
            Integer need = req == null ? null : req.requiredToefl;
            if (need == null) {
                return new LanguageEval(false, 75.0, "语言：托福 " + score + "（未配置门槛）");
            }
            double diff = score - need;
            if (diff >= 10) {
                return new LanguageEval(false, 95.0, "语言：托福 " + score + " ≥ " + need);
            }
            if (diff >= 5) {
                return new LanguageEval(false, 88.0, "语言：托福 " + score + " ≥ " + need);
            }
            if (diff >= 0) {
                return new LanguageEval(false, 78.0, "语言：托福 " + score + " ≥ " + need);
            }
            if (diff >= -5) {
                return new LanguageEval(false, 60.0, "语言：托福 " + score + " < " + need + "（可作为冲刺风险）");
            }
            return new LanguageEval(true, 20.0, "语言：托福 " + score + " < " + need + "（硬性不达标）");
        }
        return new LanguageEval(false, 65.0, "语言：" + test + " " + score + "（暂未识别）");
    }

    private TestEval evaluateStandardizedTest(MatchingRequest.StudentInfoDTO student, RequirementProfile req) {
        if (student == null) {
            return new TestEval(70.0, "标化：未填写");
        }
        Integer requiredGmat = req == null ? null : req.requiredGmat;
        Integer requiredGre = req == null ? null : req.requiredGre;
        if (requiredGmat == null && requiredGre == null) {
            return new TestEval(75.0, "标化：未配置门槛");
        }

        String test = student.getGmatGre();
        Integer score = student.getGmatGreScore();
        if (test == null || test.trim().isEmpty() || score == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("标化：未填写");
            if (requiredGmat != null) {
                sb.append("；GMAT参考 ").append(requiredGmat);
            }
            if (requiredGre != null) {
                sb.append("；GRE参考 ").append(requiredGre);
            }
            return new TestEval(55.0, sb.toString());
        }

        String t = test.trim().toLowerCase(Locale.ROOT);
        if (t.contains("gmat")) {
            if (requiredGmat == null) {
                return new TestEval(78.0, "标化：GMAT " + score + "（未配置GMAT门槛）");
            }
            int diff = score - requiredGmat;
            if (diff >= 50) {
                return new TestEval(95.0, "标化：GMAT " + score + " ≥ " + requiredGmat);
            }
            if (diff >= 0) {
                return new TestEval(85.0, "标化：GMAT " + score + " ≥ " + requiredGmat);
            }
            if (diff >= -30) {
                return new TestEval(60.0, "标化：GMAT " + score + " < " + requiredGmat + "（可作为冲刺风险）");
            }
            return new TestEval(35.0, "标化：GMAT " + score + " < " + requiredGmat);
        }
        if (t.contains("gre")) {
            if (requiredGre == null) {
                return new TestEval(78.0, "标化：GRE " + score + "（未配置GRE门槛）");
            }
            int diff = score - requiredGre;
            if (diff >= 10) {
                return new TestEval(95.0, "标化：GRE " + score + " ≥ " + requiredGre);
            }
            if (diff >= 0) {
                return new TestEval(85.0, "标化：GRE " + score + " ≥ " + requiredGre);
            }
            if (diff >= -5) {
                return new TestEval(60.0, "标化：GRE " + score + " < " + requiredGre + "（可作为冲刺风险）");
            }
            return new TestEval(35.0, "标化：GRE " + score + " < " + requiredGre);
        }
        return new TestEval(65.0, "标化：" + test + " " + score + "（暂未识别）");
    }

    private double calculateBackgroundScore(MatchingRequest.StudentInfoDTO student, RequirementProfile req) {
        if (student == null) {
            return 55.0;
        }
        double score = 55.0;
        if (student.getInternshipExperience() != null && !student.getInternshipExperience().trim().isEmpty()) {
            score += 10.0;
        }
        if (student.getResearchExperience() != null && !student.getResearchExperience().trim().isEmpty()) {
            score += 10.0;
        }
        if (student.getAwards() != null && !student.getAwards().trim().isEmpty()) {
            score += 8.0;
        }
        if (student.getPublications() != null && !student.getPublications().trim().isEmpty()) {
            score += 10.0;
        }
        if (req != null && req.requiredWorkYears != null) {
            Integer years = parseWorkYearsFromStudent(student.getWorkExperience());
            if (years == null) {
                score -= 20.0;
            } else if (years >= req.requiredWorkYears) {
                score += 10.0;
            } else {
                score -= 10.0;
            }
        } else if (student.getWorkExperience() != null && !student.getWorkExperience().trim().isEmpty()) {
            score += 6.0;
        }
        return clamp(score, 0.0, 100.0);
    }

    private Integer parseWorkYearsFromStudent(String workExperienceText) {
        if (workExperienceText == null || workExperienceText.trim().isEmpty()) {
            return null;
        }
        Matcher m = WORK_YEARS_PATTERN.matcher(workExperienceText);
        if (m.find()) {
            Integer v = safeParseInt(m.group(1));
            if (v != null && v >= 0 && v <= 20) {
                return v;
            }
        }
        return 1;
    }

    private String determineMatchLevel(double score) {
        if (score >= 90.0) {
            return "保底";
        }
        if (score >= 75.0) {
            return "稳妥";
        }
        if (score >= 60.0) {
            return "冲刺";
        }
        return "不建议";
    }

    private Double calculateAdmissionProbability(double score) {
        if (score >= 90) {
            return 0.98;
        }
        if (score >= 75) {
            return 0.85;
        }
        if (score >= 60) {
            return 0.50;
        }
        return 0.10;
    }

    private String generateRiskAnalysis(
            MatchingRequest.StudentInfoDTO student,
            School school,
            RequirementProfile requirements,
            Double studentGpaPercent,
            double requiredGpa,
            LanguageEval languageEval,
            TestEval testEval,
            double backgroundScore,
            double matchScore) {

        StringBuilder sb = new StringBuilder();
        sb.append("【").append(getSchoolName()).append("匹配分析】\n");
        sb.append("专业：")
                .append(requirements == null ? "" : (requirements.majorName == null ? "" : requirements.majorName))
                .append("\n");

        String gpaText;
        if (student == null || student.getGpa() == null) {
            gpaText = "未填写";
        } else if ("100".equals(student.getGpaScale())) {
            gpaText = String.format(Locale.ROOT, "%.1f/100", student.getGpa());
        } else {
            gpaText = String.format(Locale.ROOT, "%.2f/%s", student.getGpa(),
                    student.getGpaScale() == null ? "?" : student.getGpaScale());
        }

        sb.append("均分：").append(gpaText);
        if (studentGpaPercent != null) {
            sb.append("（折算 ").append(String.format(Locale.ROOT, "%.1f", studentGpaPercent)).append("%）");
        }
        sb.append("\n");
        sb.append("均分门槛：").append((int) Math.round(requiredGpa)).append("%");
        String src = requirements == null ? null : requirements.requirementSource;
        if ("UK_RULE_ENGINE".equals(src)) {
            sb.append("（来源：规则库）");
        } else if ("MAJOR_TEXT".equals(src)) {
            sb.append("（来源：专业入学要求）");
        } else if ("SCHOOL_TEXT".equals(src)) {
            sb.append("（来源：学校入学要求）");
        } else if ("SCHOOL_GROUP_RULE".equals(src)) {
            sb.append("（来源：学校分组规则）");
        } else if ("SCHOOL_RULE_FALLBACK".equals(src)) {
            sb.append("（来源：学校内置规则）");
        } else {
            sb.append("（来源：默认规则）");
        }
        sb.append("\n");

        if (languageEval != null) {
            sb.append(languageEval.summary).append("\n");
        }

        if (testEval != null) {
            sb.append(testEval.summary).append("\n");
        }

        if (requirements != null && requirements.requiredWorkYears != null) {
            Integer years = parseWorkYearsFromStudent(student == null ? null : student.getWorkExperience());
            sb.append("工作经验：");
            if (years == null) {
                sb.append("未填写");
            } else {
                sb.append(years).append("年");
            }
            sb.append("；要求：≥").append(requirements.requiredWorkYears).append("年\n");
        }

        sb.append("背景加分：").append(String.format(Locale.ROOT, "%.1f", backgroundScore)).append("/100\n");
        sb.append("综合得分：").append(String.format(Locale.ROOT, "%.1f", matchScore)).append("/100\n");

        if (languageEval != null && languageEval.hardFail) {
            sb.append("风险提示：语言成绩不满足硬性要求，建议优先补齐语言条件。");
            return sb.toString();
        }

        double gpa = studentGpaPercent == null ? -1.0 : studentGpaPercent;
        if (gpa < 0.0) {
            sb.append("风险提示：未填写均分，结果仅为参考。");
            return sb.toString();
        }
        if (gpa >= requiredGpa) {
            sb.append("结论：核心门槛（均分）达标，可作为主申/稳妥梯度。");
        } else if (gpa >= requiredGpa - 2) {
            sb.append("结论：均分略低于门槛，可作为冲刺，需用科研/实习/文书弥补。");
        } else {
            sb.append("结论：均分差距较大，不建议作为主申，优先考虑同梯度更友好学校。");
        }
        return sb.toString();
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private static Integer safeParseInt(String s) {
        if (s == null) {
            return null;
        }
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private static Double safeParseDouble(String s) {
        if (s == null) {
            return null;
        }
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    protected UndergradSchool resolveUndergradSchoolOrNull(String raw) {
        if (undergradSchoolMapper == null) {
            return null;
        }
        String norm = normalizeUndergradAlias(raw);
        if (norm.isEmpty()) {
            return null;
        }
        return undergradSchoolMapper.findByAliasNorm(norm);
    }

    protected static class GroupRuleDecision {
        private final String groupCode;
        private final String degreeReq;
        private final String majorCategory;
        private final Double requiredGpaPercent;
        private final boolean reject;

        protected GroupRuleDecision(String groupCode, String degreeReq, String majorCategory, Double requiredGpaPercent,
                boolean reject) {
            this.groupCode = groupCode;
            this.degreeReq = degreeReq;
            this.majorCategory = majorCategory;
            this.requiredGpaPercent = requiredGpaPercent;
            this.reject = reject;
        }
    }

    private static final Pattern UG_EN_NON_ALNUM = Pattern.compile("[^a-z0-9]+");
    private static final Pattern UG_ZH_TRIM_CHARS = Pattern.compile("[\\s·•・,，.。/\\\\()（）\\[\\]【】{}<>《》“”\"'‘’\\-—_]+");
    private static final Pattern UG_ZH_HAS_CHAR = Pattern.compile("[\\u4e00-\\u9fff]");

    private static String normalizeUndergradAlias(String raw) {
        if (raw == null) {
            return "";
        }
        String s = raw.trim();
        if (s.isEmpty()) {
            return "";
        }
        s = Normalizer.normalize(s, Normalizer.Form.NFKC);
        if (UG_ZH_HAS_CHAR.matcher(s).find()) {
            s = UG_ZH_TRIM_CHARS.matcher(s).replaceAll("");
            return s;
        }
        s = s.toLowerCase(Locale.ROOT);
        s = s.replace("&", "and");
        s = s.replace("’", "'").replace("'", "");
        s = UG_EN_NON_ALNUM.matcher(s).replaceAll(" ").trim();
        s = s.replaceAll("\\s+", "_");
        return s;
    }
}
