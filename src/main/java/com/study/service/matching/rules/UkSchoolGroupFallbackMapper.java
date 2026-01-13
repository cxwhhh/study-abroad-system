package com.study.service.matching.rules;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UkSchoolGroupFallbackMapper {
    UkSchoolGroupFallback findByTargetSchoolId(@Param("targetSchoolId") Long targetSchoolId);
}

