package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKWestminsterMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "威斯敏斯特大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("University of Westminster", "Westminster");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_WESTMINSTER_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 72.0;
    }
}
