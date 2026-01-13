package com.study.service.applicationschool;

import java.util.List;

public interface ApplicationSchoolService {
    ApplicationSchool submit(ApplicationSchool applicationSchool);

    List<ApplicationSchool> getSubmittedApplications(Long userId, Long studentId, String country);

    boolean updateApplicationStatus(Long id, String status);

    ApplicationSchool getApplicationDetail(Long id);

    boolean updateVisaInfoProvided(Long id, Boolean visaInfoProvided);

    boolean checkTablesJoined(Long userId);

    boolean deleteApplication(Long id);
}
