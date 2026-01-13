package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKCoventryMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "考文垂大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("Coventry University", "Coventry");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_COVENTRY_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 73.0;
    }
}
