package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKImperialMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "帝国理工学院";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("Imperial College London", "Imperial", "ICL");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_IMPERIAL_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 87.0;
    }
}
