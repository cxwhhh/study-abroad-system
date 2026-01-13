package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKBangorMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "班戈大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("Bangor University", "Bangor");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_BANGOR_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 72.0;
    }
}
