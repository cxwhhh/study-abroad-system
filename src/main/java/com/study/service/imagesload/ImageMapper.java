package com.study.service.imagesload;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ImageMapper {
    void insertImage(
        @Param("imageName") String imageName,
        @Param("filePath") String filePath,
        @Param("originalName") String originalName,
        @Param("fileSize") Long fileSize,
        @Param("fileType") String fileType,
        @Param("userId") Long userId
    );

    List<ImageDTO> selectAllImages(@Param("userId") Long userId);
    List<ImageDTO> selectImagesByUserId(@Param("userId") Long userId);
    List<ImageDTO> selectAllImagesWithoutUser();
} 