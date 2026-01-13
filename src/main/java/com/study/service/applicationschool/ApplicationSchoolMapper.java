package com.study.service.applicationschool;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ApplicationSchoolMapper {

        int insert(ApplicationSchool applicationSchool);

        List<ApplicationSchool> findByUserIdAndCountry(@Param("userId") Long userId,
                        @Param("country") String country);

        List<ApplicationSchool> findByUserId(@Param("userId") Long userId);

        int updateStatus(@Param("id") Long id, @Param("status") String status);

        @Select("SELECT a.*, " +
                        "s.name as student_name, s.english_name, s.gender, " +
                        "s.birth_date, s.nationality, s.passport_no, " +
                        "s.phone, s.email as student_email, s.wechat, " +
                        "s.current_school, s.major as student_major, s.gpa, " +
                        "u.username as user_name, u.email as user_email " +
                        "FROM application_schools a " +
                        "LEFT JOIN students s ON a.student_id = s.id " +
                        "LEFT JOIN users u ON a.user_id = u.id " +
                        "WHERE a.id = #{id}")
        ApplicationSchool findDetailById(@Param("id") Long id);

        @Select("SELECT COUNT(*) FROM (" +
                        "SELECT a.id FROM application_schools a " +
                        "INNER JOIN students s ON a.student_id = s.id " +
                        "INNER JOIN users u ON a.user_id = u.id " +
                        "WHERE a.user_id = #{userId}) t")
        int checkJoinStatus(@Param("userId") Long userId);

        @Select("SELECT " +
                        "a.*, " +
                        "s.name as student_name, " +
                        "s.email as student_email, " +
                        "s.level as student_level, " +
                        "u.username, " +
                        "u.email as user_email " +
                        "FROM application_schools a " +
                        "INNER JOIN students s ON a.student_id = s.id " +
                        "INNER JOIN users u ON a.user_id = u.id " +
                        "LIMIT 1")
        ApplicationSchool testJoinQuery();

        @Update("UPDATE application_schools SET visa_info_provided = #{visaInfoProvided}, " +
                        "update_time = NOW() WHERE id = #{id}")
        int updateVisaInfoProvided(@Param("id") Long id, @Param("visaInfoProvided") Boolean visaInfoProvided);

        @Delete("DELETE FROM application_schools WHERE id = #{id}")
        int deleteById(@Param("id") Long id);

        List<ApplicationSchool> findByStudentId(@Param("studentId") Long studentId);
}