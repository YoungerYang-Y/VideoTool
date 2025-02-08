package com.yang.video.service;

import org.springframework.web.multipart.MultipartFile;

public interface VideoService {
    String upload(MultipartFile file);
}
