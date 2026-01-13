package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKOxfordBrookesMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "牛津布鲁克斯大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("Oxford Brookes University", "Oxford Brookes");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_OXFORD_BROOKES_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 74.0;
    }
}
