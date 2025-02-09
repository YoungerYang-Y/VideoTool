package com.yang.video.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;

/**
 * 文件清理调度器类，用于定期清理指定目录下的旧文件
 */
@Slf4j(topic = "com.yang.video.task")
@Component
public class FileCleanupScheduler {
    // 指定要清理的目录
    private static final String DIRECTORY_PATH = System.getProperty("user.dir") + File.separator + "uploads";
    // 1 天
    private static final int DAYS_THRESHOLD = 1;

    /**
     * 定时任务方法，用于删除旧文件
     * 该方法每天凌晨3点执行，检查并删除指定目录下超过一天的文件
     */
    @Scheduled(cron = "0 0 3 * * ?") // 每天凌晨 3 点执行
    public void deleteOldFiles() {
        // 确保目录不为空
        File directory = new File(DIRECTORY_PATH);
        if (!directory.exists() || !directory.isDirectory()) {
            log.warn("目录不存在或不是目录: {}", DIRECTORY_PATH);
            return;
        }

        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            log.info("目录 {} 为空，无需清理", DIRECTORY_PATH);
            return;
        }

        // 计算一天前的时间点
        Instant oneDayAgo = Instant.now().minusSeconds(DAYS_THRESHOLD * 24L * 60L * 60L);
        // 初始化删除文件计数器
        int deletedCount = 0;

        // 遍历目录下的所有文件
        for (File file : files) {
            try {
                // 读取文件的基本属性
                BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                Instant fileTime = attrs.lastModifiedTime().toInstant();

                // 比较文件最后修改时间与一天前的时间，决定是否删
                if (fileTime.isBefore(oneDayAgo)) {
                    Path filePath = file.toPath();
                    Files.delete(filePath);
                    log.info("已删除文件: {}", file.getAbsolutePath());
                    deletedCount++;
                }
            } catch (IOException e) {
                // 处理IO异常，记录错误日志
                log.error("无法删除文件 {}: {}", file.getAbsolutePath(), e.getMessage());
            } catch (Exception e) {
                // 处理其他异常，记录错误日志
                log.error("处理文件 {} 失败: {}", file.getAbsolutePath(), e.getMessage());
            }
        }

        // 记录清理完成的日志
        log.info("清理完成，共删除 {} 个文件", deletedCount);
    }
}
