package com.yang.video.config;

import com.yang.video.interceptor.ConcurrencyLimitInterceptor;
import com.yang.video.interceptor.DiskSpaceInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    /**
     * 空闲磁盘检测
     */
    private final DiskSpaceInterceptor diskSpaceInterceptor;

    /**
     * 并发限制
     */
    private final ConcurrencyLimitInterceptor concurrencyLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(diskSpaceInterceptor)
                .addPathPatterns("/upload");

        registry.addInterceptor(concurrencyLimitInterceptor)
                .addPathPatterns("/**");
    }
}
