package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKSussexMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "苏塞克斯大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("University of Sussex", "Sussex");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_SUSSEX_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 75.0;
    }
}
