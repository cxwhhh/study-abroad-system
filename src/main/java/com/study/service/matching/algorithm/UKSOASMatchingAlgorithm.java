package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKSOASMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "伦敦大学亚非学院";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("SOAS University of London", "SOAS", "School of Oriental and African Studies");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_SOAS_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 75.0;
    }
}
