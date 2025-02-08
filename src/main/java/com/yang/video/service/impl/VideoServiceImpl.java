package com.yang.video.service.impl;

import com.yang.video.exception.ServiceException;
import com.yang.video.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class VideoServiceImpl implements VideoService {
    @Override
    public String upload(MultipartFile file) {
        // 获取文件原始名称
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isValidVideoFile(originalFilename)) {
            log.warn("上传文件时发生错误或文件格式不正确");
            throw new ServiceException(400, "上传文件时发生错误或文件格式不正确");
        }

        // 设置上传文件的保存路径，这里保存到项目的根目录
        Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads");
        File directory = uploadDir.toFile();
        if (!directory.exists()) {
            // 创建目录
            directory.mkdirs();
        }

        String fileExtension = getFileExtension(originalFilename);
        String newFileName = UUID.randomUUID() + "." + fileExtension;

        // 保存文件
        Path destFilePath = uploadDir.resolve(newFileName);

        try {
            // 保存文件到指定路径
            Files.copy(file.getInputStream(), destFilePath);
        } catch (IOException e) {
            log.error("上传文件时发生错误: {}", e.getMessage(), e);
            throw new ServiceException(500, "上传文件失败: " + e.getMessage());
        }
        return newFileName;
    }

    private boolean isValidVideoFile(String filename) {
        // 验证文件扩展名是否为允许的视频格式
        String[] allowedExtensions = {"mp4", "avi", "mkv"};
        String extension = getFileExtension(filename).toLowerCase();
        for (String ext : allowedExtensions) {
            if (ext.equals(extension)) {
                return true;
            }
        }
        return false;
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
}
