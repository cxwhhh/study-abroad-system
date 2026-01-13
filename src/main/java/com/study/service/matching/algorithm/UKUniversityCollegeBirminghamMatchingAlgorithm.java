package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKUniversityCollegeBirminghamMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "伯明翰大学学院";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("University College Birmingham", "UCB", "College Birmingham");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_UNIVERSITY_COLLEGE_BIRMINGHAM_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 68.0;
    }
}
