package com.study.service.imagesload;

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
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class ImageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final ImageMapper imageMapper;

    public ImageService(ImageMapper imageMapper) {
        this.imageMapper = imageMapper;
    }

    @Transactional
    public String storeImage(MultipartFile file, Long userId) throws IOException {
        log.info("开始处理图片上传: {}", file.getOriginalFilename());
        
        // 确保根目录存在
        Path rootPath = Paths.get(uploadDir, "images");  // 在upload-dir下创建images子目录
        if (!Files.exists(rootPath)) {
            Files.createDirectories(rootPath);
            log.info("创建根目录: {}", rootPath);
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String newFilename = generateUniqueFilename(fileExtension);

        // 按用户ID创建子目录
        Path userPath = rootPath.resolve(userId.toString());
        if (!Files.exists(userPath)) {
            Files.createDirectories(userPath);
            log.info("创建用户目录: {}", userPath);
        }

        // 完整的文件存储路径
        Path filePath = userPath.resolve(newFilename);
        log.info("图片将保存到: {}", filePath);
        
        try {
            // 保存文件
            Files.copy(file.getInputStream(), filePath);

            // 构建数据库存储的路径
            String dbPath = "/api/images/" + userId + "/" + newFilename;

            // 保存到数据库
            try {
                imageMapper.insertImage(
                    newFilename, 
                    dbPath, 
                    originalFilename, 
                    file.getSize(), 
                    fileExtension,
                    userId
                );
                log.info("图片信息已保存到数据库，用户ID: {}", userId);
            } catch (Exception e) {
                // 如果数据库保存失败，删除已上传的文件
                Files.deleteIfExists(filePath);
                log.error("保存到数据库失败，已删除上传的文件", e);
                throw e;
            }

            log.info("图片上传完成，数据库路径: {}", dbPath);
            return dbPath;
        } catch (Exception e) {
            log.error("图片上传过程中发生错误", e);
            throw e;
        }
    }

    public List<ImageDTO> getImagesByUserId(Long userId) {
        log.info("开始获取用户 {} 的图片列表", userId);
        try {
            List<ImageDTO> images = imageMapper.selectAllImages(userId);
            log.info("从数据库获取到 {} 个图片记录", images.size());
            
            // 过滤掉不存在的图片
            return images.stream()
                .filter(image -> {
                    String relativePath = image.getFilePath().replace("/api/images/", "");
                    Path imagePath = Paths.get(uploadDir, "images", relativePath);
                    boolean exists = Files.exists(imagePath);
                    if (!exists) {
                        log.warn("图片文件不存在: {}", imagePath);
                    }
                    return exists;
                })
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            log.error("获取图片列表时发生错误", e);
            throw e;
        }
    }

    private String generateUniqueFilename(String fileExtension) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String random = UUID.randomUUID().toString().substring(0, 8);
        return timestamp + "_" + random + "." + fileExtension;
    }

    private String getFileExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1).toLowerCase())
                .orElse("jpg");
    }
}