package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKHullMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "赫尔大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("University of Hull", "Hull");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_HULL_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 70.0;
    }
}
