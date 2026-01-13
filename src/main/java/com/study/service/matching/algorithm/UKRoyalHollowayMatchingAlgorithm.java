package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKRoyalHollowayMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "伦敦大学皇家霍洛威学院";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("Royal Holloway, University of London", "Royal Holloway", "RHUL");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_ROYAL_HOLLOWAY_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 74.0;
    }
}
