package com.yang.video.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;

@Slf4j(topic = "com.yang.video.task")
@Component
public class FileCleanupScheduler {
    // 指定要清理的目录
    private static final String DIRECTORY_PATH = Paths.get(System.getProperty("user.dir"), "uploads").toString();
    // 1 天
    private static final int DAYS_THRESHOLD = 1;

    @Scheduled(cron = "0 0 3 * * ?") // 每天凌晨 3 点执行
    public void deleteOldFiles() {
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

        Instant threeDaysAgo = Instant.now().minusSeconds(DAYS_THRESHOLD * 24L * 60L * 60L);
        int deletedCount = 0;

        for (File file : files) {
            try {
                BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                Instant fileTime = attrs.lastModifiedTime().toInstant();

                if (fileTime.isBefore(threeDaysAgo)) {
                    Path filePath = file.toPath();
                    Files.delete(filePath);
                    log.info("已删除文件: {}", file.getAbsolutePath());
                    deletedCount++;
                }
            } catch (IOException e) {
                log.error("无法删除文件 {}: {}", file.getAbsolutePath(), e.getMessage());
            } catch (Exception e) {
                log.error("处理文件 {} 失败: {}", file.getAbsolutePath(), e.getMessage());
            }
        }

        log.info("清理完成，共删除 {} 个文件", deletedCount);
    }
}
