package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKStirlingMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "斯特灵大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("University of Stirling", "Stirling");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_STIRLING_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 73.0;
    }
}
