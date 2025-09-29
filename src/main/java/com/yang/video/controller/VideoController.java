package com.yang.video.controller;

import com.yang.video.dto.Response;
import com.yang.video.dto.UploadResponse;
import com.yang.video.service.VideoService;
import com.yang.video.util.FileNameValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "视频接口", description = "视频上传与下载接口")
public class VideoController {

    private final VideoService videoService;

    private static final String FILE_DIRECTORY = System.getProperty("user.dir") + File.separator + "uploads";

    /**
     * 文件上传
     *
     * @param file 上传的文件
     * @return 标准响应格式
     */
    @PostMapping("/upload")
    @Operation(summary = "上传视频文件", description = "上传单个视频文件，提取BGM并返回BGM文件信息")
    @ApiResponse(responseCode = "200", description = "上传成功",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    @ApiResponse(responseCode = "400", description = "文件为空或非法")
    public ResponseEntity<Response<UploadResponse>> upload(
            @Parameter(description = "要上传的视频文件", required = true)
            @RequestParam("file") MultipartFile file) {
        log.debug("文件上传开始");

        if (file.isEmpty()) {
            log.warn("文件为空，请选择一个视频文件上传");
            return ResponseEntity.badRequest().body(Response.error(400, "文件为空，请选择一个视频文件上传"));
        }

        UploadResponse uploadResponse = videoService.upload(file);

        log.info("文件上传成功，文件名：{}", uploadResponse.getFilename());
        return ResponseEntity.ok(Response.success("文件上传成功", uploadResponse));
    }

    /**
     * 下载文件
     * <p>
     * 根据提供的文件名下载文件如果文件名不合法或文件不存在，将返回相应的HTTP错误码
     *
     * @param filename 要下载的文件名
     * @return 包含文件的响应体如果请求的文件名不合法或文件不可用，则返回错误响应
     */
    @GetMapping("/download/{filename}")
    @Operation(summary = "下载视频文件", description = "根据文件名下载已上传的视频文件")
    @ApiResponse(responseCode = "200", description = "下载成功",
            content = @Content(mediaType = "application/octet-stream"))
    @ApiResponse(responseCode = "400", description = "文件名不合法")
    @ApiResponse(responseCode = "404", description = "文件不存在")
    public ResponseEntity<?> downloadFile(
            @Parameter(description = "要下载的文件名", required = true, example = "video_123.mp4")
            @PathVariable String filename) {
        // 检查文件名是否合法
        if (!FileNameValidator.isValidFilename(filename)) {
            log.warn("Invalid filename: {}", filename);
            return ResponseEntity.badRequest().body(Response.error(400, "文件名不合法"));
        }

        try {
            // 从文件名中提取日期
            String dateStr = FileNameValidator.extractDateFromFilename(filename);
            Path filePath;
            
            if (dateStr != null) {
                // 如果文件名包含日期，则按日期目录结构查找文件
                // 文件路径：/uploads/2025-09-29/2025-09-29_772f9446-a9f9-4508-9f78-aa0e64222d81.mp3
                filePath = Paths.get(FILE_DIRECTORY).resolve(dateStr).resolve(filename).normalize();
                log.debug("File path with date directory: {}", filePath);
            } else {
                // 如果文件名不包含日期，则直接在根目录查找（向后兼容）
                filePath = Paths.get(FILE_DIRECTORY).resolve(filename).normalize();
                log.debug("File path in root directory: {}", filePath);
            }
            
            Resource resource = new UrlResource(filePath.toUri());

            // 检查文件是否存在且可读
            if (!resource.exists() || !resource.isReadable()) {
                log.warn("File not found or not readable: {}", filePath);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.error(404, "文件不存在"));
            }

            // 设置响应头，以指示浏览器下载文件
            log.info("File found and ready for download: {}", filename);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"").body(resource);
        } catch (MalformedURLException e) {
            // 处理URL异常
            log.warn("Invalid URL: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.error(500, "服务器内部错误"));
        }
    }

}
