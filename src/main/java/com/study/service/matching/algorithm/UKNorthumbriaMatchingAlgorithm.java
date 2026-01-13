package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKNorthumbriaMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "诺森比亚大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("Northumbria University", "Northumbria");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_NORTHUMBRIA_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 70.0;
    }
}
