package com.yang.video.controller;

import com.yang.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/video")
public class VideoController {

    private final VideoService videoService;

    // 定义文件上传接口
    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {

        log.debug("文件上传开始");

        if (file.isEmpty()) {
            log.warn("文件为空，请选择一个视频文件上传");
            return ResponseEntity.badRequest().body("文件为空，请选择一个视频文件上传");
        }

        // 限制文件大小，文件超过100M时拒绝上传
        if (file.getSize() > 1024 * 1024 * 100) {
            log.warn("文件大小超过100M，请选择一个较小的视频文件上传");
            return ResponseEntity.badRequest().body("文件大小超过100M，请选择一个较小的视频文件上传");
        }

        String newFilename = videoService.upload(file);

        log.info("文件上传成功，文件名：{}", newFilename);
        return ResponseEntity.ok("文件上传成功，文件名：" + newFilename);
    }
}
