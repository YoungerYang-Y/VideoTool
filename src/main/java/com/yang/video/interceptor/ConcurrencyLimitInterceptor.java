package com.yang.video.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class ConcurrencyLimitInterceptor implements HandlerInterceptor {
    // 设置最大并发请求数为 3
    private static final int MAX_CONCURRENT_REQUESTS = 3;

    // 使用 AtomicInteger 以确保线程安全
    private static final AtomicInteger currentConcurrentRequests = new AtomicInteger(0);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取当前并发请求数
        int currentRequests = currentConcurrentRequests.get();

        if (currentRequests >= MAX_CONCURRENT_REQUESTS) {
            // 如果当前并发请求数已经达到上限，拒绝请求
            log.warn("当前并发请求数({})已达到最大限制({})，请求被拒绝。", currentRequests, MAX_CONCURRENT_REQUESTS);
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE); // 503 Service Unavailable
            response.getWriter().write("服务器当前处理的请求过多，请稍后再试。");
            return false; // 阻止请求继续
        }

        // 否则，允许请求继续，并将并发请求数增加 1
        currentConcurrentRequests.incrementAndGet();
        log.debug("当前并发请求数: {}，允许请求继续。", currentRequests + 1);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求处理完成后，减少当前并发请求数
        currentConcurrentRequests.decrementAndGet();
        log.debug("请求处理完成，当前并发请求数: {}", currentConcurrentRequests.get());
    }
}
