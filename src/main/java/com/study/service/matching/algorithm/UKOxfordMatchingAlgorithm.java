package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKOxfordMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "牛津大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("University of Oxford", "Oxford");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_OXFORD_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 88.0;
    }
}
