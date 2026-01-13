package com.study.service.files;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface FileMapper {
    void insertFile(FileDTO fileDTO);

    List<FileDTO> selectAllFiles(@Param("userId") Long userId);

    List<FileDTO> selectFilesByUserId(@Param("userId") Long userId);
    
    /**
     * 根据学生ID查询文件
     * @param studentId 学生ID
     * @return 文件列表
     */
    List<FileDTO> selectFilesByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 根据学生ID和申请ID查询文件
     * @param studentId 学生ID
     * @param applicationId 申请ID
     * @return 文件列表
     */
    List<FileDTO> selectFilesByStudentAndApplicationId(@Param("studentId") Long studentId, @Param("applicationId") Long applicationId);
    
    /**
     * 根据文件ID查询文件
     * @param fileId 文件ID
     * @return 文件信息
     */
    FileDTO selectFileById(@Param("fileId") Long fileId);
    
    /**
     * 根据文件ID删除文件
     * @param fileId 文件ID
     * @return 影响的行数
     */
    int deleteFileById(@Param("fileId") Long fileId);
}