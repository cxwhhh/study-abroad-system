package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKCreativeArtsMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "创意艺术大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("University for the Creative Arts", "UCA", "Creative Arts");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_CREATIVE_ARTS_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 70.0;
    }
}
