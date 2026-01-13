package com.study.service.matching.rules;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UkSchoolUndergradGroupMapper {
    String findGroupCode(@Param("targetSchoolId") Long targetSchoolId,
                         @Param("undergradSchoolId") Long undergradSchoolId);
}

