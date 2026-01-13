package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKAstonMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "阿斯顿大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("Aston University", "Aston");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_ASTON_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 75.0;
    }
}
