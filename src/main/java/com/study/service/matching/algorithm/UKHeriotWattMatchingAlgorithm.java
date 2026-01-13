package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKHeriotWattMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "赫瑞瓦特大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("Heriot-Watt University", "Heriot Watt University", "Heriot-Watt", "Heriot Watt");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_HERIOT_WATT_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 75.0;
    }
}
