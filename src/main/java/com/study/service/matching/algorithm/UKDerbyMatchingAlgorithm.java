package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKDerbyMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "德比大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("University of Derby", "Derby");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_DERBY_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 70.0;
    }
}
