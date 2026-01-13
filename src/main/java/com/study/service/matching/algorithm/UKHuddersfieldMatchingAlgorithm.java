package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKHuddersfieldMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "哈德斯菲尔德大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("University of Huddersfield", "Huddersfield");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_HUDDERSFIELD_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 70.0;
    }
}
