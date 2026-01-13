package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKDundeeMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "邓迪大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("University of Dundee", "Dundee");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_DUNDEE_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 72.0;
    }
}
