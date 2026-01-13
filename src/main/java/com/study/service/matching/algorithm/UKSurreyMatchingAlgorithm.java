package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKSurreyMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "萨里大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("University of Surrey", "Surrey");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_SURREY_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 75.0;
    }
}
