package com.yang.video.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import com.yang.video.exception.ServiceException;
import com.yang.video.service.VideoService;
import com.yang.video.util.FFmpegUtils;
import com.yang.video.util.FileNameValidator;
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
        // 获取文件扩展名
        String fileExtension = FileUtil.extName(originalFilename);
        if (originalFilename == null || !FileNameValidator.isValidVideoFile(fileExtension)) {
            log.warn("上传文件时发生错误或文件格式不正确");
            throw new ServiceException(400, "上传文件时发生错误或文件格式不正确");
        }

        // 设置上传文件的保存路径，这里保存到项目的根目录
        Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads", DateUtil.today());
        File directory = uploadDir.toFile();
        if (!directory.exists()) {
            // 创建目录并检查是否成功
            boolean created = directory.mkdirs();
            if (!created) {
                log.error("创建上传目录失败: {}", uploadDir);
                throw new ServiceException(500, "创建上传目录失败: " + uploadDir);
            }
        }

        // 生成新的文件名，使用UUID以避免文件名冲突
        String newFileName = DateUtil.today() + StrPool.UNDERLINE + UUID.randomUUID() + StrPool.DOT + fileExtension;

        // 保存文件
        Path destFilePath = uploadDir.resolve(newFileName);

        try {
            // 保存文件到指定路径
            Files.copy(file.getInputStream(), destFilePath);
        } catch (IOException e) {
            log.error("上传文件时发生错误: {}", e.getMessage(), e);
            throw new ServiceException(500, "上传文件失败: " + e.getMessage());
        }

        getBGM(destFilePath.toFile());

        // 返回文件的下载路径
        return UriComponentsBuilder.fromUriString(ServletUriComponentsBuilder.fromCurrentServletMapping().toUriString()).path("/api/video/download").queryParam("filename", newFileName).toUriString();
    }

    /**
     * 通过FFmpeg获取背景音乐
     */
    @Override
    public void getBGM(File inputVideoFile) {
        String outputAudioFilePath = CharSequenceUtil.subBefore(inputVideoFile.getPath(), StrPool.DOT, true) + StrPool.DOT + "mp3";
        File outputAudioFile = FileUtil.newFile(outputAudioFilePath);
        try {
            FFmpegUtils.extractBGM(inputVideoFile, outputAudioFile);
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
            // 保留中断状态
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
