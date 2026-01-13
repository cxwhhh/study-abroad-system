package com.study.service.matching.rules;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UndergradSchoolMapper {
    UndergradSchool findById(@Param("id") Long id);

    UndergradSchool findByAliasNorm(@Param("aliasNorm") String aliasNorm);
}

