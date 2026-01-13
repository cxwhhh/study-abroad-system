package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKEssexMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "埃塞克斯大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("University of Essex", "Essex");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_ESSEX_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 73.0;
    }
}
