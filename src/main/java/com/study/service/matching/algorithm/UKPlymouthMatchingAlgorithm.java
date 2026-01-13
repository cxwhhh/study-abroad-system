package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKPlymouthMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "普利茅斯大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("University of Plymouth", "Plymouth");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_PLYMOUTH_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 70.0;
    }
}
