package com.yang.video.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FileNameValidator {

    // 添加私有构造函数，防止实例化
    private FileNameValidator() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static boolean isValidFilename(String filename) {
        // 不能为空
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }

        // 禁止目录遍历攻击
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return false;
        }

        // 仅允许字母、数字、常见符号（防止特殊字符注入）
        String regex = "^[a-zA-Z0-9._-]+$";
        return filename.matches(regex);
    }

    /**
     * 检查文件是否是允许的视频文件格式
     *
     * @param extName 文件扩展名
     * @return 如果文件格式符合允许的视频格式，则返回true；否则返回false
     */
    public static boolean isValidVideoFile(String extName) {
        // 验证文件扩展名是否为允许的视频格式
        String[] allowedExtensions = {"mp4", "avi", "mkv"};
        for (String ext : allowedExtensions) {
            if (ext.equals(extName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从文件名中提取日期部分
     * 文件名格式：YYYY-MM-DD_uuid.ext
     * 例如：2025-09-29_772f9446-a9f9-4508-9f78-aa0e64222d81.mp3
     *
     * @param filename 文件名
     * @return 日期字符串（YYYY-MM-DD格式），如果无法提取则返回null
     */
    public static String extractDateFromFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return null;
        }

        try {
            // 查找第一个下划线的位置
            int underscoreIndex = filename.indexOf('_');
            if (underscoreIndex == -1) {
                return null;
            }

            // 提取日期部分（下划线之前的部分）
            String datePart = filename.substring(0, underscoreIndex);
            
            // 验证日期格式是否为 YYYY-MM-DD
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(datePart, formatter);
            
            return datePart;
        } catch (DateTimeParseException e) {
            return null;
        }
    }

}
