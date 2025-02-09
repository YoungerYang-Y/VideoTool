package com.yang.video.service.impl;

import com.yang.video.exception.ServiceException;
import com.yang.video.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * VideoServiceImpl类实现了VideoService接口，提供视频上传和下载的服务
 */
@Slf4j
@Service
public class VideoServiceImpl implements VideoService {
    /**
     * 上传视频文件
     *
     * @param file 要上传的视频文件
     * @return 返回上传成功后的文件下载路径
     * @throws ServiceException 如果文件上传过程中发生错误或文件格式不正确，则抛出此异常
     */
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
            // 创建目录并检查是否成功
            boolean created = directory.mkdirs();
            if (!created) {
                log.error("创建上传目录失败: {}", uploadDir);
                throw new ServiceException(500, "创建上传目录失败: " + uploadDir);
            }
        }

        // 获取文件扩展名
        String fileExtension = getFileExtension(originalFilename);
        // 生成新的文件名，使用UUID以避免文件名冲突
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

        // 返回文件的下载路径
        return UriComponentsBuilder.fromUriString(ServletUriComponentsBuilder.fromCurrentServletMapping().toUriString()).path("/api/video/download")
                .queryParam("filename", newFileName)
                .toUriString();
    }

    /**
     * 检查文件是否是允许的视频文件格式
     *
     * @param filename 文件名
     * @return 如果文件格式符合允许的视频格式，则返回true；否则返回false
     */
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

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 文件的扩展名如果文件没有扩展名，则返回空字符串
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
}
