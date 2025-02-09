package com.yang.video.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Slf4j
@Component
public class DiskSpaceFilter implements Filter {

    // 3GB
    private static final long MIN_DISK_SPACE = 3L * 1024L * 1024L * 1024L;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        File file = new File("/");
        long freeSpace = file.getUsableSpace();

        if (freeSpace < MIN_DISK_SPACE) {
            log.warn("磁盘空间不足，剩余空间: {} bytes", freeSpace);
            HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
            httpServletResponse.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            httpServletResponse.getWriter().write("服务器磁盘空间不足，无法处理请求！");
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
