package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKGoldsmithsMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "伦敦大学金史密斯学院";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("Goldsmiths, University of London", "Goldsmiths", "University of London Goldsmiths");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_GOLDSMITHS_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 74.0;
    }
}
