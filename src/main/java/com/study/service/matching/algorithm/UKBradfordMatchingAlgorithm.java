package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKBradfordMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "布拉德福德大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("University of Bradford", "Bradford");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_BRADFORD_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 72.0;
    }
}
