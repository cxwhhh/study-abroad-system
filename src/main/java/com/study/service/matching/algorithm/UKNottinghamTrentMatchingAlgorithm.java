package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKNottinghamTrentMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "诺丁汉特伦特大学";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("Nottingham Trent University", "Nottingham Trent", "NTU");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_NOTTINGHAM_TRENT_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 70.0;
    }
}
