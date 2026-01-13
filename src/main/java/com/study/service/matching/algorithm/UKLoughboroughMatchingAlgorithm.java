package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKLoughboroughMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "拉夫堡大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("Loughborough University", "Loughborough");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_LOUGHBOROUGH_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 78.0;
    }
}
