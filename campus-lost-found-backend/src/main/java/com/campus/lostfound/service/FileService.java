package com.campus.lostfound.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文件存储服务接口
 */
public interface FileService {

    /**
     * 上传单张图片
     *
     * @param file 上传的文件
     * @return 包含访问 URL 和文件信息的 Map
     */
    Map<String, Object> uploadImage(MultipartFile file);
}
