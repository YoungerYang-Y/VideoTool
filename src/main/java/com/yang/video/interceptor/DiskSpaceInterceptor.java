package com.yang.video.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.File;
import java.nio.file.FileSystems;

@Slf4j
@Component
public class DiskSpaceInterceptor implements HandlerInterceptor {
    // 设置磁盘空间阈值（单位：字节），当磁盘剩余空间低于此值时阻止请求
    private static final long MIN_FREE_SPACE = 5L * 1024 * 1024 * 1024; // 5GB

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取磁盘的根目录（可根据需要更改为其他路径）
        File fileSystemRoot = FileSystems.getDefault().getPath("/").toFile();
        long freeSpace = fileSystemRoot.getUsableSpace();

        // 打印磁盘空间信息
        log.debug("当前磁盘剩余空间: {}", freeSpace);

        // 判断剩余磁盘空间是否满足要求
        if (freeSpace < MIN_FREE_SPACE) {
            // 磁盘空间不足，打印警告日志
            log.warn("磁盘剩余空间不足！当前可用空间: {} bytes，低于设定的阈值: {} bytes，阻止请求继续处理。", freeSpace, MIN_FREE_SPACE);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 设置错误状态码
            response.getWriter().write("服务器磁盘空间不足，请稍后再试。");
            return false; // 阻止请求继续
        }

        return true; // 允许请求继续
    }
}
