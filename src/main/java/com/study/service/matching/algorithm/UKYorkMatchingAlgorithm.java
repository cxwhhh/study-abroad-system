package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKYorkMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "约克大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("University of York", "York");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_YORK_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 78.0;
    }
}
