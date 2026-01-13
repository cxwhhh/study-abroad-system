package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKManchesterMetropolitanMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "曼彻斯特城市大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("Manchester Metropolitan University", "Manchester Metropolitan", "MMU");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_MANCHESTER_METROPOLITAN_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 70.0;
    }
}
