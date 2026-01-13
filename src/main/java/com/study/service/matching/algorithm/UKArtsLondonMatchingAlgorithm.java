package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKArtsLondonMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "伦敦艺术大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("University of the Arts London", "UAL", "Arts London");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_ARTS_LONDON_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 74.0;
    }
}
