package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKUlsterMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "阿尔斯特大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("Ulster University", "Ulster");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_ULSTER_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 70.0;
    }
}
