package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKBathMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "巴斯大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("University of Bath", "Bath");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_BATH_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 79.0;
    }
}
