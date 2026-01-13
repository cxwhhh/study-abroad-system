package com.study.service.files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

@Service
@Slf4j
public class FileService {

    @Value("${file.upload-dir:xym}")
    private String uploadBaseDir;

    private final FileMapper fileMapper;

    // 材料类型到英文文件名的映射
    private static final Map<String, String> MATERIAL_TYPE_MAPPING = new HashMap<>();
    static {
        MATERIAL_TYPE_MAPPING.put("personalStatement", "personal_statement");
        MATERIAL_TYPE_MAPPING.put("resume", "resume");
        MATERIAL_TYPE_MAPPING.put("languageScores", "language_scores");
        MATERIAL_TYPE_MAPPING.put("transcript", "transcript");
        MATERIAL_TYPE_MAPPING.put("recommendationLetters", "recommendation_letter");
        MATERIAL_TYPE_MAPPING.put("portfolio", "portfolio");
        MATERIAL_TYPE_MAPPING.put("papers", "research_papers");
        MATERIAL_TYPE_MAPPING.put("others", "other");
        MATERIAL_TYPE_MAPPING.put("passport", "passport");
        MATERIAL_TYPE_MAPPING.put("idCard", "id_card");
    }

    // 反向映射：从文件名到材料类型
    private static final Map<String, String> REVERSE_MATERIAL_TYPE_MAPPING = new HashMap<>();
    static {
        for (Map.Entry<String, String> entry : MATERIAL_TYPE_MAPPING.entrySet()) {
            REVERSE_MATERIAL_TYPE_MAPPING.put(entry.getValue(), entry.getKey());
        }
    }

    public FileService(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    /**
     * 存储申请材料文件
     * 
     * @param file          上传的文件
     * @param materialType  材料类型
     * @param studentId     学生ID
     * @param applicationId 申请学校ID
     * @return 包含文件ID和路径的Map
     * @throws IOException 如果文件读写出错/**
     *                     存储返点申请验证文件
     */
    @Transactional
    public Map<String, Object> storeRebateVerifyFile(MultipartFile file, String materialType, Long applicationId)
            throws IOException {
        try {
            String normalizedMaterialType = normalizeMaterialType(materialType);
            log.info("开始存储返点申请验证文件: {}, 类型: {}, 申请ID: {}", file.getOriginalFilename(), normalizedMaterialType,
                    applicationId);

            // 创建根目录
            Path rootPath = Paths.get(uploadBaseDir, "rebate_verify");
            Files.createDirectories(rootPath);
            log.info("创建或确认根目录: {}", rootPath.toAbsolutePath());

            // 生成有意义的文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String newFilename = generateMeaningfulFilename(normalizedMaterialType, fileExtension);

            // 创建目录结构: rebate_verify/applicationId
            String applicationIdStr = applicationId.toString();

            // 创建申请目录
            Path applicationDir = rootPath.resolve(applicationIdStr);
            Files.createDirectories(applicationDir);
            log.info("创建或确认申请目录: {}", applicationDir);

            // 文件相对路径
            String relativePath = "rebate_verify/" + applicationIdStr;

            // 目标文件路径
            Path targetFile = applicationDir.resolve(newFilename);
            log.info("目标文件路径: {}", targetFile);

            // 保存文件
            Files.copy(file.getInputStream(), targetFile);
            log.info("文件已成功保存到: {}", targetFile);

            // 数据库中的路径，用于访问
            String dbPath = relativePath + "/" + newFilename;

            // 使用材料类型作为描述
            String description = "返点申请验证: " + normalizedMaterialType;

            // 创建FileDTO对象
            FileDTO fileDTO = new FileDTO();
            fileDTO.setFileName(newFilename);
            fileDTO.setFilePath(dbPath);
            fileDTO.setOriginalName(originalFilename);
            fileDTO.setFileSize(file.getSize());
            fileDTO.setFileType(fileExtension);
            fileDTO.setDescription(description);
            fileDTO.setUserId(null);
            fileDTO.setStudentId(null); // 返点申请不需要studentId
            fileDTO.setApplicationId(applicationId);

            // 将文件信息保存到数据库并获取生成的文件ID
            fileMapper.insertFile(fileDTO);
            Long fileId = fileDTO.getId();
            log.info("返点申请验证文件信息已保存到数据库，文件ID: {}, 访问路径: {}", fileId, dbPath);

            // 返回文件ID和路径
            Map<String, Object> result = new HashMap<>();
            result.put("fileId", fileId);
            result.put("filePath", dbPath);
            return result;
        } catch (IOException e) {
            log.error("存储返点申请验证文件失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 存储申请材料文件
     */
    @Transactional
    public Map<String, Object> storeApplicationFile(MultipartFile file, String materialType, Long studentId,
            Long applicationId) throws IOException {
        String normalizedMaterialType = normalizeMaterialType(materialType);
        log.info("开始处理申请材料上传: {}, 材料类型: {}, 学生ID: {}, 申请学校ID: {}",
                file.getOriginalFilename(), normalizedMaterialType, studentId, applicationId);

        if (studentId == null || applicationId == null) {
            log.error("未提供学生ID或申请ID，无法存储文件");
            throw new IllegalArgumentException("必须提供学生ID和申请ID");
        }

        try {
            // 确保根目录存在
            Path rootPath = Paths.get(uploadBaseDir);
            Files.createDirectories(rootPath);
            log.info("创建或确认根目录: {}", rootPath.toAbsolutePath());

            // 生成有意义的文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String newFilename = generateMeaningfulFilename(normalizedMaterialType, fileExtension);

            // 创建目录结构: studentId/applicationId
            String studentIdStr = studentId.toString();
            String applicationIdStr = applicationId.toString();

            // 创建学生目录
            Path studentDir = rootPath.resolve(studentIdStr);
            Files.createDirectories(studentDir);
            log.info("创建或确认学生目录: {}", studentDir);

            // 创建申请目录（使用申请ID）
            Path applicationDir = studentDir.resolve(applicationIdStr);
            Files.createDirectories(applicationDir);
            log.info("创建或确认申请目录: {}", applicationDir);

            // 文件相对路径
            String relativePath = studentIdStr + "/" + applicationIdStr;

            // 目标文件路径
            Path targetFile = applicationDir.resolve(newFilename);
            log.info("目标文件路径: {}", targetFile);

            // 保存文件
            Files.copy(file.getInputStream(), targetFile);
            log.info("文件已成功保存到: {}", targetFile);

            // 数据库中的路径，用于访问
            String dbPath = relativePath + "/" + newFilename;

            // 使用材料类型作为描述
            String description = "申请材料: " + normalizedMaterialType;

            // 创建FileDTO对象
            FileDTO fileDTO = new FileDTO();
            fileDTO.setFileName(newFilename);
            fileDTO.setFilePath(dbPath);
            fileDTO.setOriginalName(originalFilename);
            fileDTO.setFileSize(file.getSize());
            fileDTO.setFileType(fileExtension);
            fileDTO.setDescription(description);
            fileDTO.setUserId(null);
            fileDTO.setStudentId(studentId);
            fileDTO.setApplicationId(applicationId);

            // 将文件信息保存到数据库并获取生成的文件ID
            fileMapper.insertFile(fileDTO);
            Long fileId = fileDTO.getId();
            log.info("文件信息已保存到数据库，文件ID: {}, 访问路径: {}", fileId, dbPath);

            // 返回文件ID和路径
            Map<String, Object> result = new HashMap<>();
            result.put("fileId", fileId);
            result.put("filePath", dbPath);
            return result;
        } catch (IOException e) {
            log.error("文件存储失败: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("文件信息保存到数据库失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取学生特定申请的文件
     */
    public List<FileDTO> getFilesByStudentAndApplicationId(Long studentId, Long applicationId) {
        if (studentId == null || applicationId == null) {
            log.error("获取文件失败：学生ID或申请ID为空");
            throw new IllegalArgumentException("学生ID和申请ID不能为空");
        }
        return fileMapper.selectFilesByStudentAndApplicationId(studentId, applicationId);
    }

    /**
     * 获取学生的所有申请文件
     */
    public List<FileDTO> getFilesByStudentId(Long studentId) {
        if (studentId == null) {
            log.error("获取文件失败：学生ID为空");
            throw new IllegalArgumentException("学生ID不能为空");
        }
        return fileMapper.selectFilesByStudentId(studentId);
    }

    /**
     * 删除文件
     * 
     * @param fileId 文件ID
     * @return 是否删除成功
     */
    @Transactional
    public boolean deleteFile(Long fileId) {
        log.info("开始删除文件: fileId={}", fileId);

        if (fileId == null) {
            log.error("删除文件失败：文件ID为空");
            throw new IllegalArgumentException("文件ID不能为空");
        }

        try {
            // 先从数据库获取文件信息
            FileDTO fileInfo = fileMapper.selectFileById(fileId);
            if (fileInfo == null) {
                log.warn("文件不存在: fileId={}", fileId);
                return false;
            }

            // 构建文件路径
            Path rootPath = Paths.get(uploadBaseDir);
            Path filePath = rootPath.resolve(fileInfo.getFilePath());

            // 删除物理文件
            boolean fileDeleted = false;
            if (Files.exists(filePath)) {
                try {
                    Files.delete(filePath);
                    fileDeleted = true;
                    log.info("物理文件删除成功: {}", filePath);
                } catch (IOException e) {
                    log.error("删除物理文件失败: {}, error: {}", filePath, e.getMessage());
                    // 即使物理文件删除失败，也继续删除数据库记录
                }
            } else {
                log.warn("物理文件不存在: {}", filePath);
                fileDeleted = true; // 文件不存在也算删除成功
            }

            // 删除数据库记录
            int deletedRows = fileMapper.deleteFileById(fileId);
            boolean dbDeleted = deletedRows > 0;

            if (dbDeleted) {
                log.info("数据库记录删除成功: fileId={}", fileId);
            } else {
                log.error("数据库记录删除失败: fileId={}", fileId);
            }

            return fileDeleted && dbDeleted;

        } catch (Exception e) {
            log.error("删除文件失败: fileId={}, error={}", fileId, e.getMessage(), e);
            throw new RuntimeException("删除文件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据材料类型生成有意义的文件名
     */
    private String generateMeaningfulFilename(String materialType, String fileExtension) {
        String baseName = MATERIAL_TYPE_MAPPING.getOrDefault(materialType, "document");
        String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        String uuid = UUID.randomUUID().toString().substring(0, 8);

        // 格式: material-type-timestamp-uuid.extension
        return baseName + "-" + timestamp + "-" + uuid + (fileExtension.isEmpty() ? "" : "." + fileExtension);
    }

    private String normalizeMaterialType(String materialType) {
        if (materialType == null) {
            return "others";
        }
        String v = materialType.trim();
        if (v.isEmpty()) {
            return "others";
        }

        for (String key : MATERIAL_TYPE_MAPPING.keySet()) {
            if (key.equalsIgnoreCase(v)) {
                return key;
            }
        }

        String lower = v.toLowerCase(Locale.ROOT);
        switch (lower) {
            case "personal_statement":
                return "personalStatement";
            case "language_scores":
                return "languageScores";
            case "recommendation_letter":
            case "recommendation_letters":
                return "recommendationLetters";
            case "research_papers":
                return "papers";
            case "id_card":
                return "idCard";
            case "other":
            case "others":
                return "others";
            default:
                break;
        }

        if (!lower.contains("_")) {
            return v;
        }

        String[] parts = lower.split("_+");
        if (parts.length == 0) {
            return v;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            String p = parts[i];
            if (p.isEmpty()) {
                continue;
            }
            sb.append(Character.toUpperCase(p.charAt(0)));
            if (p.length() > 1) {
                sb.append(p.substring(1));
            }
        }

        String camel = sb.toString();
        if ("recommendationLetter".equals(camel)) {
            return "recommendationLetters";
        }
        if ("languageScore".equals(camel)) {
            return "languageScores";
        }
        if ("researchPapers".equals(camel) || "researchPaper".equals(camel)) {
            return "papers";
        }
        if ("other".equals(camel)) {
            return "others";
        }
        return camel;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty() || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * 从文件描述中获取材料类型
     */
    public String getMaterialTypeFromDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            return "others";
        }

        String desc = description.toLowerCase().trim();

        // 直接匹配材料类型
        if (REVERSE_MATERIAL_TYPE_MAPPING.containsKey(desc)) {
            return REVERSE_MATERIAL_TYPE_MAPPING.get(desc);
        }

        // 模糊匹配
        if (desc.contains("personal") || desc.contains("statement")) {
            return "personalStatement";
        } else if (desc.contains("resume") || desc.contains("cv")) {
            return "resume";
        } else if (desc.contains("language") || desc.contains("toefl") || desc.contains("ielts") || desc.contains("gre")
                || desc.contains("gmat")) {
            return "languageScores";
        } else if (desc.contains("transcript") || desc.contains("成绩单")) {
            return "transcript";
        } else if (desc.contains("recommendation") || desc.contains("推荐信")) {
            return "recommendationLetters";
        } else if (desc.contains("portfolio") || desc.contains("作品集")) {
            return "portfolio";
        } else if (desc.contains("paper") || desc.contains("research") || desc.contains("论文")) {
            return "papers";
        } else if (desc.contains("passport") || desc.contains("护照")) {
            return "passport";
        } else if (desc.contains("id") || desc.contains("身份证")) {
            return "idCard";
        }

        // 如果没有找到匹配的，返回others
        return "others";
    }
}
