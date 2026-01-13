package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKKentMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "肯特大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("University of Kent", "Kent");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_KENT_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 73.0;
    }
}
