package com.yang.video.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FFmpegUtils {

    private FFmpegUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 使用ffmpeg命令提取视频文件中的背景音乐
     *
     * @param inputVideoFile  输入视频文件
     * @param outputAudioFile 输出音频文件
     */
    public static void extractBGM(File inputVideoFile, File outputAudioFile) throws IOException, InterruptedException {
        // 设置音频的编码质量，明确指定输出中包含输入文件中的音频流
        ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", "-i", inputVideoFile.getPath(), "-q:a", "0", "-map", "a", outputAudioFile.getPath());
        // 执行ffmpeg命令
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        // 异步读取输出流（重要！避免阻塞）
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // 实时监控输出
                    log.warn("[FFmpeg] {}", line);
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }).start();
        // 读取ffmpeg命令的错误输出
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String line;
        while ((line = errorReader.readLine()) != null) {
            log.error(line);
        }

        // 等待命令执行完成并获取退出代码
        boolean exitCode = process.waitFor(3L, TimeUnit.SECONDS);
        if (exitCode) {
            log.info("BGM extracted successfully.");
        } else {
            log.warn("Failed to extract BGM. Exit Code: {}", exitCode);
        }
    }
}
