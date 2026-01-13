package com.study.service.files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

@RestController
@RequestMapping("/api/files")
@Slf4j
public class FileController {

    @Value("${file.upload-dir:xym}")
    private String uploadBaseDir;

    @Autowired
    private FileService fileService;

    /**
     * 上传申请材料
     */
    @PostMapping("/application/materials/student/{studentId}/application-school/{applicationId}")
    public ResponseEntity<?> uploadApplicationMaterial(
            @PathVariable Long studentId,
            @PathVariable Long applicationId,
            @RequestPart("file") MultipartFile file,
            @RequestParam("type") String materialType) {
        log.info("接收到申请材料上传请求: fileName={}, size={}, materialType={}, studentId={}, applicationId={}",
                file != null ? file.getOriginalFilename() : "null", 
                file != null ? file.getSize() : "null", 
                materialType, studentId, applicationId);
        
        // 检查是否为multipart请求
        if (file == null) {
            log.error("未检测到文件，可能不是multipart请求");
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "未检测到文件，请确保请求类型为multipart/form-data");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        
        try {
            // 保存文件
            Map<String, Object> fileResult = fileService.storeApplicationFile(file, materialType, studentId, applicationId);
            String filePath = (String) fileResult.get("filePath");
            Long fileId = (Long) fileResult.get("fileId");
            log.info("申请材料上传成功: {}, 文件ID: {}, 类型: {}, 申请ID: {}", filePath, fileId, materialType, applicationId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("filePath", filePath);
            response.put("fileId", fileId);
            response.put("materialType", materialType);
            response.put("applicationId", applicationId);
            response.put("message", "申请材料上传成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("申请材料上传失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "申请材料上传失败: " + e.getMessage());
            response.put("error", e.getClass().getName());
            response.put("errorDetail", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 获取学生的所有申请材料
     */
    @GetMapping("/application/materials/student/{studentId}")
    public ResponseEntity<?> getStudentMaterials(@PathVariable Long studentId) {
        log.info("获取学生ID={}的申请材料", studentId);
        try {
            List<FileDTO> files = fileService.getFilesByStudentId(studentId);
            
            // 根据材料类型分组
            Map<String, List<FileDTO>> materialsByType = new HashMap<>();
            
            for (FileDTO file : files) {
                String description = file.getDescription();
                if (description != null && description.startsWith("申请材料: ")) {
                    String type = description.substring("申请材料: ".length());
                    List<FileDTO> typeFiles = materialsByType.getOrDefault(type, new java.util.ArrayList<>());
                    typeFiles.add(file);
                    materialsByType.put(type, typeFiles);
                }
            }
            
            return ResponseEntity.ok(materialsByType);
        } catch (Exception e) {
            log.error("获取学生申请材料失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "获取申请材料失败: " + e.getMessage()));
        }
    }

    /**
     * 获取指定学生和申请ID的材料
     */
    @GetMapping("/application/materials/student/{studentId}/application-school/{applicationId}")
    public ResponseEntity<?> getApplicationMaterials(
            @PathVariable Long studentId,
            @PathVariable Long applicationId) {
        log.info("获取学生ID={}，申请学校ID={}的材料", studentId, applicationId);
        try {
            List<FileDTO> files = fileService.getFilesByStudentAndApplicationId(studentId, applicationId);
            
            // 根据材料类型分组
            Map<String, List<FileDTO>> materialsByType = new HashMap<>();
            
            for (FileDTO file : files) {
                String description = file.getDescription();
                if (description != null && description.startsWith("申请材料: ")) {
                    String type = description.substring("申请材料: ".length());
                    List<FileDTO> typeFiles = materialsByType.getOrDefault(type, new java.util.ArrayList<>());
                    typeFiles.add(file);
                    materialsByType.put(type, typeFiles);
                }
            }
            
            return ResponseEntity.ok(materialsByType);
        } catch (Exception e) {
            log.error("获取申请材料失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "获取申请材料失败: " + e.getMessage()));
        }
    }

    /**
     * 下载或查看文件
     * 路径格式: /api/files/download/{studentId}/{applicationId}/{filename}
     */
    @GetMapping("/download/{studentId}/{applicationId}/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable("studentId") String studentId,
            @PathVariable("applicationId") String applicationId,
            @PathVariable("filename") String filename,
            @RequestParam(value = "download", required = false) Boolean download) {
        try {
            Path filePath = Paths.get(uploadBaseDir, studentId, applicationId, filename);
            log.info("请求访问文件: {}", filePath);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = determineContentType(filename);
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header("Content-Disposition",
                                (download != null && download) ? "attachment; filename=\"" + filename + "\""
                                        : "inline; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                log.error("文件不存在或不可读: {}", filePath);
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            log.error("文件访问失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 检查文件是否存在 (HEAD请求)
     */
    @RequestMapping(value = "/download/{studentId}/{applicationId}/{filename:.+}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkFileExists(
            @PathVariable String studentId,
            @PathVariable String applicationId,
            @PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadBaseDir, studentId, applicationId, filename);
            log.info("检查文件是否存在: {}", filePath);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = determineContentType(filename);
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .build();
            } else {
                log.error("文件不存在或不可读: {}", filePath);
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            log.error("文件访问失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable Long fileId) {
        log.info("删除文件请求: fileId={}", fileId);
        try {
            boolean deleted = fileService.deleteFile(fileId);
            
            Map<String, Object> response = new HashMap<>();
            if (deleted) {
                response.put("code", 200);
                response.put("message", "文件删除成功");
                response.put("success", true);
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 404);
                response.put("message", "文件不存在或删除失败");
                response.put("success", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            log.error("删除文件失败: fileId={}, error={}", fileId, e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "删除文件失败: " + e.getMessage());
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取学生材料状态（供admin端使用）
     */
    @GetMapping("/admin/materials/student/{studentId}/application/{applicationId}")
    public ResponseEntity<?> getStudentMaterialsStatus(
            @PathVariable Long studentId,
            @PathVariable Long applicationId) {
        log.info("获取学生ID={}，申请学校ID={}的材料状态", studentId, applicationId);
        try {
            List<FileDTO> files = fileService.getFilesByStudentAndApplicationId(studentId, applicationId);
            
            // 根据材料类型分组并转换为前端期望的格式
            Map<String, Object> materialStatus = new HashMap<>();
            
            // 定义材料类型映射（从前端type到后端返回的key）
            Map<String, String> typeMapping = new HashMap<>();
            typeMapping.put("passport", "passport");
            typeMapping.put("idCard", "id_card");
            typeMapping.put("transcript", "transcript");
            typeMapping.put("personalStatement", "personal_statement");
            typeMapping.put("recommendationLetters", "recommendation_letter");
            typeMapping.put("resume", "resume");
            typeMapping.put("languageScores", "language_scores");
            typeMapping.put("papers", "research_papers");
            typeMapping.put("others", "other");
            
            // 按材料类型分组
            Map<String, List<FileDTO>> materialsByType = new HashMap<>();
            for (FileDTO file : files) {
                String description = file.getDescription();
                if (description != null && description.startsWith("申请材料: ")) {
                    String type = description.substring("申请材料: ".length());
                    List<FileDTO> typeFiles = materialsByType.getOrDefault(type, new java.util.ArrayList<>());
                    typeFiles.add(file);
                    materialsByType.put(type, typeFiles);
                }
            }
            
            // 转换为前端期望的格式
            for (Map.Entry<String, String> entry : typeMapping.entrySet()) {
                String frontendType = entry.getKey();
                String backendKey = entry.getValue();
                List<FileDTO> typeFiles = materialsByType.get(frontendType);
                
                if (typeFiles != null && !typeFiles.isEmpty()) {
                    // 取第一个文件作为代表
                    FileDTO file = typeFiles.get(0);
                    Map<String, Object> status = new HashMap<>();
                    status.put("uploaded", true);
                    status.put("fileName", file.getFileName());
                    status.put("uploadTime", file.getUploadTime());
                    status.put("fileSize", file.getFileSize());
                    status.put("fileId", file.getId());
                    status.put("filePath", file.getFilePath());
                    materialStatus.put(backendKey, status);
                } else {
                    // 未上传的材料
                    Map<String, Object> status = new HashMap<>();
                    status.put("uploaded", false);
                    status.put("fileName", null);
                    status.put("uploadTime", null);
                    status.put("fileSize", null);
                    status.put("fileId", null);
                    status.put("filePath", null);
                    materialStatus.put(backendKey, status);
                }
            }
            
            // 返回前端期望的格式
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", materialStatus);
            response.put("message", "获取材料状态成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取学生材料状态失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取材料状态失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 管理员代为上传申请材料
     */
    @PostMapping("/admin/materials/student/{studentId}/application/{applicationId}")
    public ResponseEntity<?> adminUploadApplicationMaterial(
            @PathVariable("studentId") Long studentId,
            @PathVariable("applicationId") Long applicationId,
            @RequestPart("file") MultipartFile file,
            @RequestParam("materialType") String materialType) {
        log.info("管理员代为上传申请材料: fileName={}, size={}, materialType={}, studentId={}, applicationId={}",
                file != null ? file.getOriginalFilename() : "null", 
                file != null ? file.getSize() : "null", 
                materialType, studentId, applicationId);
        
        // 检查是否为multipart请求
        if (file == null) {
            log.error("未检测到文件，可能不是multipart请求");
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "未检测到文件，请确保请求类型为multipart/form-data");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        
        try {
            // 保存文件
            Map<String, Object> fileResult = fileService.storeApplicationFile(file, materialType, studentId, applicationId);
            String filePath = (String) fileResult.get("filePath");
            Long fileId = (Long) fileResult.get("fileId");
            log.info("管理员代为上传申请材料成功: {}, 文件ID: {}, 类型: {}, 申请ID: {}", filePath, fileId, materialType, applicationId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("filePath", filePath);
            response.put("fileId", fileId);
            response.put("materialType", materialType);
            response.put("applicationId", applicationId);
            response.put("message", "管理员代为上传申请材料成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("管理员代为上传申请材料失败: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "管理员代为上传申请材料失败: " + e.getMessage());
            response.put("error", e.getClass().getName());
            response.put("errorDetail", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



    /**
     * 根据文件名确定内容类型
     */
    private String determineContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "pdf":
                return "application/pdf";
            case "doc":
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "txt":
                return "text/plain;charset=UTF-8";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "svg":
                return "image/svg+xml";
            case "mp4":
                return "video/mp4";
            case "webm":
                return "video/webm";
            case "mp3":
                return "audio/mpeg";
            case "wav":
                return "audio/wav";
            default:
                return "application/octet-stream";
        }
    }
}
