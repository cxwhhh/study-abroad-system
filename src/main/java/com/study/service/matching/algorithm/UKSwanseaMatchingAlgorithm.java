package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKSwanseaMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "斯旺西大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("Swansea University", "Swansea");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_SWANSEA_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 72.0;
    }
}
