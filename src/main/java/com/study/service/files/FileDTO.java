package com.study.service.files;

import lombok.Data;
import java.util.Date;

@Data
public class FileDTO {
    private Long id;
    private String fileName;
    private String filePath;
    private String originalName;
    private Date uploadTime;
    private Long fileSize;
    private String fileType;
    private String description;
    private Long userId;
    private Long studentId;
    private Long applicationId;
} 