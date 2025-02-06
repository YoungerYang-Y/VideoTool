package com.yang.video.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

//@Slf4j
@RestController
@RequestMapping("/api/video")
public class VideoController {

    private static final Logger log = LogManager.getLogger(VideoController.class);

    // 定义文件上传接口
    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file) {

        log.debug("文件上传开始");
        
        if (file.isEmpty()) {
            log.warn("文件为空，请选择一个视频文件上传");
            return ResponseEntity.badRequest().body("文件为空，请选择一个视频文件上传");
        }

        // 获取文件原始名称
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            log.warn("上传文件时发生错误");
            return ResponseEntity.badRequest().body("上传文件时发生错误");
        }

        // 设置上传文件的保存路径，这里保存到项目的根目录
        String uploadDir = System.getProperty("user.dir") + "/uploads/";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            // 创建目录
            directory.mkdirs();
        }

        // 保存文件
        File destFile = new File(uploadDir + fileName);
        try {
            // 保存文件到指定路径
            file.transferTo(destFile);
        } catch (IOException e) {
            log.error("上传文件时发生错误: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("上传文件失败: " + e.getMessage());
        }

        log.info("文件上传成功，文件名：{}", fileName);
        return ResponseEntity.ok("文件上传成功，文件名：" + fileName);
    }
}
