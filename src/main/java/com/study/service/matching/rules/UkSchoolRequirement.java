package com.study.service.matching.rules;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UkSchoolRequirement {
    private Long id;
    private Long targetSchoolId;
    private String groupCode;
    private String degreeReq;
    private String majorCategory;
    private Double requiredScore;
    private Boolean isReject;
    private String version;
    private Date effectiveFrom;
    private Date effectiveTo;
    private Integer priority;
    private Date createdAt;
    private Date updatedAt;
}

