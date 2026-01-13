package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKBirkbeckMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "伦敦大学伯贝克学院";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("Birkbeck, University of London", "Birkbeck", "University of London Birkbeck");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_BIRKBECK_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 75.0;
    }
}
