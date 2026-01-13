package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKQMULMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "伦敦玛丽女王大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("Queen Mary University of London", "QMUL", "Queen Mary");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_QMUL_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 76.0;
    }
}
