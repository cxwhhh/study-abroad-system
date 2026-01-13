package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import com.study.service.matching.dto.MatchingRequest;
import com.study.service.matching.rules.UndergradSchool;
import com.study.service.major.Major;
import com.study.service.schools.School;

import java.util.Arrays;
import java.util.List;

@Service
public class UKBristolMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "布里斯托大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("University of Bristol", "Bristol");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_BRISTOL_MATCHING_ALGORITHM";
    }

    @Override
    protected boolean preferSchoolGroupRulesOverText() {
        return true;
    }

    @Override
    protected GroupRuleDecision schoolFallbackGroupDecision(
            MatchingRequest request,
            School school,
            Major major,
            String majorName,
            String majorReqText,
            String schoolReqText) {
        String group = resolveGroup(request);
        String degreeReq = resolveDegreeReqFromTexts(majorReqText, schoolReqText);
        String majorCategory = resolveSimpleMajorCategory(majorName, major == null ? null : major.getEnglishName());
        Double required = requiredGpaForGroup(group, degreeReq, majorCategory);
        boolean reject = required == null;
        return new GroupRuleDecision(group, degreeReq, majorCategory, required, reject);
    }

    private String resolveGroup(MatchingRequest request) {
        String raw = request == null || request.getStudentInfo() == null ? null
                : request.getStudentInfo().getUndergraduateSchool();
        UndergradSchool u = resolveUndergradSchoolOrNull(raw);
        if (u != null) {
            if (Boolean.TRUE.equals(u.getIs985())) {
                return "A";
            }
            if (Boolean.TRUE.equals(u.getIs211()) || Boolean.TRUE.equals(u.getIsDoubleFirstClass())) {
                return "B";
            }
            String st = u.getSchoolType() == null ? "" : u.getSchoolType().trim().toLowerCase();
            if ("private".equals(st) || "民办".equals(st) || "independent".equals(st) || "独立学院".equals(st)
                    || "independent_college".equals(st)) {
                return "D";
            }
            if ("public".equals(st) || "公办".equals(st)) {
                return "C";
            }
            return "C";
        }
        if (raw != null) {
            String t = raw.toLowerCase();
            if (t.contains("985")) {
                return "A";
            }
            if (t.contains("211") || t.contains("双一流")) {
                return "B";
            }
        }
        return "C";
    }

    private static String resolveSimpleMajorCategory(String cn, String en) {
        String text = ((cn == null ? "" : cn) + " " + (en == null ? "" : en)).toLowerCase();
        if (text.contains("computer") || text.contains("comput") || text.contains("software")
                || text.contains("data") || text.contains("ai") || text.contains("engineering")
                || text.contains("计算机") || text.contains("软件") || text.contains("数据")
                || text.contains("人工智能") || text.contains("工程")) {
            return "ECS_ENGINEERING";
        }
        if (text.contains("finance") || text.contains("financial") || text.contains("bank")
                || text.contains("econom") || text.contains("account") || text.contains("management")
                || text.contains("marketing") || text.contains("金融") || text.contains("经济")
                || text.contains("会计") || text.contains("管理") || text.contains("市场")) {
            return "BUSINESS_ECON";
        }
        return null;
    }

    private static Double requiredGpaForGroup(String group, String degreeReq, String majorCategory) {
        double base;
        if ("A".equalsIgnoreCase(group)) {
            base = 80.0;
        } else if ("B".equalsIgnoreCase(group)) {
            base = 82.0;
        } else if ("C".equalsIgnoreCase(group)) {
            base = 85.0;
        } else if ("D".equalsIgnoreCase(group)) {
            base = 88.0;
        } else {
            base = 85.0;
        }

        if ("BUSINESS_ECON".equals(majorCategory)) {
            base += 1.0;
        }
        if ("2:2".equals(degreeReq)) {
            base -= 5.0;
        }
        return base;
    }
}
