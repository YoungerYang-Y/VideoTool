package com.yang.video.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface VideoService {
    String upload(MultipartFile file);

    void getBGM(File inputVideoFile);
}
