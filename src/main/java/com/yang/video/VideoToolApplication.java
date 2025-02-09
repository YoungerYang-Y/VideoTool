package com.yang.video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class VideoToolApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoToolApplication.class, args);
    }

}
