package com.yang.video.controller;

import com.yang.video.service.VideoService;
import com.yang.video.util.FileNameValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/video")
public class VideoController {

    private final VideoService videoService;

    private static final String FILE_DIRECTORY = System.getProperty("user.dir") + File.separator + "uploads";

    /**
     * 文件上传
     *
     * @param file 上传的文件
     * @return 文件名
     */
    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        log.debug("文件上传开始");

        if (file.isEmpty()) {
            log.warn("文件为空，请选择一个视频文件上传");
            return ResponseEntity.badRequest().body("文件为空，请选择一个视频文件上传");
        }

        String newFilename = videoService.upload(file);

        log.info("文件上传成功，文件名：{}", newFilename);
        return ResponseEntity.ok("文件上传成功，文件名：" + newFilename);
    }

    /**
     * 下载文件
     * <p>
     * 根据提供的文件名下载文件如果文件名不合法或文件不存在，将返回相应的HTTP错误码
     *
     * @param filename 要下载的文件名
     * @return 包含文件的响应体如果请求的文件名不合法或文件不可用，则返回错误响应
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String filename) {
        // 检查文件名是否合法
        if (!FileNameValidator.isValidFilename(filename)) {
            log.warn("Invalid filename: {}", filename);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        try {
            // 构建文件路径并创建资源对象
            Path filePath = Paths.get(FILE_DIRECTORY).resolve(filename).normalize();
            log.debug("File path: {}", filePath);
            Resource resource = new UrlResource(filePath.toUri());

            // 检查文件是否存在且可读
            if (!resource.exists() || !resource.isReadable()) {
                log.warn("File not found or not readable: {}", filePath);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // 设置响应头，以指示浏览器下载文件
            log.info("File found and ready for download: {}", filename);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"").body(resource);
        } catch (MalformedURLException e) {
            // 处理URL异常
            log.warn("Invalid URL: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
