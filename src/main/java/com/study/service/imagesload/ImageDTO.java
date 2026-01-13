package com.study.service.imagesload;

import lombok.Data;
import java.util.Date;

@Data
public class ImageDTO {
    private Long id;
    private String imageName;
    private String filePath;
    private String originalName;
    private Date uploadTime;
    private Long fileSize;
    private String fileType;
    private Long userId;
    private Long studentId;
}