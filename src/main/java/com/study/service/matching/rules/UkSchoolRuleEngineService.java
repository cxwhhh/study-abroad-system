package com.study.service.matching.rules;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class UkSchoolRuleEngineService {

    private static final Pattern EN_NON_ALNUM = Pattern.compile("[^a-z0-9]+");
    private static final Pattern ZH_TRIM_CHARS = Pattern.compile("[\\s·•・,，.。/\\\\()（）\\[\\]【】{}<>《》“”\"'‘’\\-—_]+");
    private static final Pattern ZH_HAS_CHAR = Pattern.compile("[\\u4e00-\\u9fff]");

    @Autowired
    private UndergradSchoolMapper undergradSchoolMapper;

    @Autowired
    private UkSchoolUndergradGroupMapper ukSchoolUndergradGroupMapper;

    @Autowired
    private UkSchoolGroupFallbackMapper ukSchoolGroupFallbackMapper;

    @Autowired
    private UkSchoolRequirementMapper ukSchoolRequirementMapper;

    public String resolveGroupCodeOrNull(Long targetSchoolId, String rawUndergradSchool) {
        if (targetSchoolId == null || rawUndergradSchool == null || rawUndergradSchool.trim().isEmpty()) {
            return null;
        }

        UndergradSchool undergrad = resolveUndergradSchool(rawUndergradSchool);
        if (undergrad == null || undergrad.getId() == null) {
            return null;
        }

        String mapped = ukSchoolUndergradGroupMapper.findGroupCode(targetSchoolId, undergrad.getId());
        if (mapped != null && !mapped.trim().isEmpty()) {
            return mapped.trim();
        }

        UkSchoolGroupFallback fallback = ukSchoolGroupFallbackMapper.findByTargetSchoolId(targetSchoolId);
        if (fallback == null) {
            return null;
        }

        String strategy = fallback.getFallbackStrategy() == null ? "" : fallback.getFallbackStrategy().trim();
        if ("TAG_BASED".equalsIgnoreCase(strategy)) {
            String g = tagBasedGroupCode(undergrad, fallback);
            if (g != null && !g.trim().isEmpty()) {
                return g.trim();
            }
        }

        if (fallback.getDefaultGroupCode() != null && !fallback.getDefaultGroupCode().trim().isEmpty()) {
            return fallback.getDefaultGroupCode().trim();
        }

        return null;
    }

    public RequirementDecision resolveRequirementOrNull(Long targetSchoolId, String groupCode, String degreeReq, String majorCategory) {
        if (targetSchoolId == null) {
            return null;
        }
        if (groupCode == null || groupCode.trim().isEmpty()) {
            return null;
        }

        UkSchoolRequirement r = ukSchoolRequirementMapper.findBest(
                targetSchoolId,
                groupCode == null ? null : groupCode.trim(),
                degreeReq == null ? null : degreeReq.trim(),
                majorCategory == null ? null : majorCategory.trim()
        );
        if (r == null) {
            return null;
        }
        boolean reject = r.getIsReject() != null && r.getIsReject();
        return new RequirementDecision(r.getRequiredScore(), reject);
    }

    private UndergradSchool resolveUndergradSchool(String raw) {
        String norm = normalizeAlias(raw);
        if (norm.isEmpty()) {
            return null;
        }
        return undergradSchoolMapper.findByAliasNorm(norm);
    }

    private static String normalizeAlias(String raw) {
        if (raw == null) {
            return "";
        }
        String s = raw.trim();
        if (s.isEmpty()) {
            return "";
        }
        s = Normalizer.normalize(s, Normalizer.Form.NFKC);
        if (ZH_HAS_CHAR.matcher(s).find()) {
            s = ZH_TRIM_CHARS.matcher(s).replaceAll("");
            return s;
        }
        s = s.toLowerCase(Locale.ROOT);
        s = s.replace("&", "and");
        s = s.replace("’", "'").replace("'", "");
        s = EN_NON_ALNUM.matcher(s).replaceAll(" ").trim();
        s = s.replaceAll("\\s+", "_");
        return s;
    }

    private static String tagBasedGroupCode(UndergradSchool undergrad, UkSchoolGroupFallback fallback) {
        if (undergrad == null || fallback == null) {
            return null;
        }
        if (undergrad.getIs985() != null && undergrad.getIs985() && notBlank(fallback.getGroupCode985())) {
            return fallback.getGroupCode985();
        }
        if (undergrad.getIs211() != null && undergrad.getIs211() && notBlank(fallback.getGroupCode211())) {
            return fallback.getGroupCode211();
        }
        if (undergrad.getIsDoubleFirstClass() != null && undergrad.getIsDoubleFirstClass()
                && notBlank(fallback.getGroupCodeDoubleFirstClass())) {
            return fallback.getGroupCodeDoubleFirstClass();
        }

        String st = undergrad.getSchoolType() == null ? "" : undergrad.getSchoolType().trim().toLowerCase(Locale.ROOT);
        if (("independent".equals(st) || "independent_college".equals(st) || "独立学院".equals(st))
                && notBlank(fallback.getGroupCodeIndependent())) {
            return fallback.getGroupCodeIndependent();
        }
        if (("private".equals(st) || "民办".equals(st))
                && notBlank(fallback.getGroupCodePrivate())) {
            return fallback.getGroupCodePrivate();
        }
        if (("public".equals(st) || "公办".equals(st))
                && notBlank(fallback.getGroupCodeOtherPublic())) {
            return fallback.getGroupCodeOtherPublic();
        }
        return null;
    }

    private static boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    @Data
    @AllArgsConstructor
    public static class RequirementDecision {
        private Double requiredScore;
        private boolean reject;
    }
}
