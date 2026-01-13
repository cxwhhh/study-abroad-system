package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKCardiffMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "卡迪夫大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("Cardiff University", "Cardiff");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_CARDIFF_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 77.0;
    }
}
