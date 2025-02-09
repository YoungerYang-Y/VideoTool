package com.yang.video.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * 磁盘空间过滤器，用于检查服务器是否有足够的磁盘空间
 * 实现了Filter接口，以便在请求处理前进行磁盘空间的检查
 */
@Slf4j
@Component
public class DiskSpaceFilter implements Filter {

    // 最小磁盘空间要求，设置为3GB
    private static final long MIN_DISK_SPACE = 3L * 1024L * 1024L * 1024L;

    /**
     * 执行过滤器的主要方法
     * 检查服务器的磁盘空间是否足够，如果不足则中断请求并返回错误信息
     *
     * @param servletRequest  Servlet请求对象，用于访问请求信息
     * @param servletResponse Servlet响应对象，用于向客户端发送响应
     * @param filterChain     过滤链对象，用于将请求传递给下一个过滤器或目标资源
     * @throws IOException      如果在执行过程中发生I/O错误
     * @throws ServletException 如果在执行过程中发生Servlet错误
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 创建File对象以根目录为基准，用以检查总的磁盘空间
        File file = new File("/");
        // 获取可用的磁盘空间
        long freeSpace = file.getUsableSpace();

        // 检查磁盘空间是否小于最小要求
        if (freeSpace < MIN_DISK_SPACE) {
            // 如果磁盘空间不足，记录警告日志
            log.warn("磁盘空间不足，剩余空间: {} bytes", freeSpace);
            // 转换响应为HttpServletResponse类型，以便设置HTTP状态码和响应体
            HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
            httpServletResponse.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            httpServletResponse.getWriter().write("服务器磁盘空间不足，无法处理请求！");
            return;
        }

        // 如果磁盘空间充足，继续执行请求处理链
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
