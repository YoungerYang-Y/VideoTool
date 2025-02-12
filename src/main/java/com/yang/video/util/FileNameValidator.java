package com.yang.video.util;

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

}
