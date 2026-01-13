package com.study.service.matching.algorithm;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UKGlasgowArtMatchingAlgorithm extends UKSingleSchoolMatchingAlgorithmBase {
    @Override
    public String getSchoolName() {
        return "格拉斯哥艺术学院";
    }

    @Override
    protected List<String> getSchoolAliases() {
        return Arrays.asList("Glasgow School of Art", "GSA", "Glasgow Art");
    }

    @Override
    protected String getAlgorithmStrategy() {
        return "UK_GLASGOW_ART_MATCHING_ALGORITHM";
    }

    @Override
    protected Double schoolFallbackRequiredGpaPercent() {
        return 72.0;
    }
}
