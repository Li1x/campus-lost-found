package com.campus.lostfound.service.impl;

import com.campus.lostfound.common.exception.BusinessException;
import com.campus.lostfound.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 文件上传服务实现类
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Value("${upload.base-path:./uploads}")
    private String uploadBasePath;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");

    @Override
    public Map<String, Object> uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw BusinessException.badRequest("上传文件不能为空");
        }

        // 1. 获取并校验文件后缀
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw BusinessException.badRequest("无效的文件格式");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw BusinessException.badRequest("仅支持 JPG、PNG、GIF、WEBP 格式的图片");
        }

        // 2. 按日期组织目录，防止单目录文件过多
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Path targetDir = Paths.get(uploadBasePath, datePath).toAbsolutePath().normalize();

        try {
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            // 3. 生成随机文件名，杜绝路径穿越与文件名覆盖
            String newFilename = UUID.randomUUID().toString().replace("-", "") + "." + extension;
            Path targetFile = targetDir.resolve(newFilename);

            file.transferTo(targetFile.toFile());

            String accessUrl = "/uploads/" + datePath + "/" + newFilename;
            log.info("图片上传成功: {}", accessUrl);

            Map<String, Object> result = new HashMap<>();
            result.put("url", accessUrl);
            result.put("originalName", originalFilename);
            result.put("size", file.getSize());
            return result;
        } catch (IOException e) {
            log.error("文件上传保存失败", e);
            throw BusinessException.badRequest("文件上传失败，请稍后重试");
        }
    }
}
