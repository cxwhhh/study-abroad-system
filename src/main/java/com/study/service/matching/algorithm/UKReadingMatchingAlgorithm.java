package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKReadingMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "雷丁大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("University of Reading", "Reading");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_READING_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 75.0;
    }
}
