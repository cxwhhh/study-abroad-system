package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKStAndrewsMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "圣安德鲁斯大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("University of St Andrews", "University of St. Andrews", "St Andrews", "St. Andrews");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_ST_ANDREWS_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 83.0;
    }
}
