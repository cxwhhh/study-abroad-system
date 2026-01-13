package com.study.service.matching.rules;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UkSchoolRequirementMapper {
    UkSchoolRequirement findBest(@Param("targetSchoolId") Long targetSchoolId,
                                 @Param("groupCode") String groupCode,
                                 @Param("degreeReq") String degreeReq,
                                 @Param("majorCategory") String majorCategory);
}

