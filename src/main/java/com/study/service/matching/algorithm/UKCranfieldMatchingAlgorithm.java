package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKCranfieldMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "克兰菲尔德大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("Cranfield University", "Cranfield");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_CRANFIELD_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 78.0;
    }
}
