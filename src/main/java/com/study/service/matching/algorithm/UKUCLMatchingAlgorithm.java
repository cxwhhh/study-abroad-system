package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKUCLMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "伦敦大学学院";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("University College London", "UCL");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_UCL_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 86.0;
    }
}
