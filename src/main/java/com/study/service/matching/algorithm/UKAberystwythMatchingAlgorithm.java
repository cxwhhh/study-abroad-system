package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKAberystwythMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "亚伯大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("Aberystwyth University", "Aberystwyth");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_ABERYSTWYTH_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 73.0;
    }
}
