package com.yang.video.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {
    /**
     * 文件名（不包含后缀）
     */
    private String filename;
    
    /**
     * 下载接口URL
     */
    private String url;
    
    /**
     * 文件后缀
     */
    private String extension;
}
