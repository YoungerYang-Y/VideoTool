package com.yang.video.service;

import com.yang.video.dto.UploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface VideoService {
    /**
     * 上传视频文件
     * @param file 要上传的视频文件
     * @return 返回上传成功后的响应信息
     */
    UploadResponse upload(MultipartFile file);

    void getBGM(File inputVideoFile);
}
