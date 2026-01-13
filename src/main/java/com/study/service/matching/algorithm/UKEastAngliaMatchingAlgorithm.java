package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKEastAngliaMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "东安格利亚大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("University of East Anglia", "UEA", "East Anglia");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_EAST_ANGLIA_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 75.0;
    }
}
