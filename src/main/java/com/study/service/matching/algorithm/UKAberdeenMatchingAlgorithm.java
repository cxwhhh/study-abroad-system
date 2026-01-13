package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKAberdeenMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "阿伯丁大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("University of Aberdeen", "Aberdeen University", "Aberdeen");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_ABERDEEN_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 75.0;
    }
}
