package com.yang.video.handler;

import com.yang.video.exception.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器类
 * 用于统一处理项目中抛出的异常，返回标准化的错误响应
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 处理业务异常
     * 当抛出ServiceException时，此方法将被捕获并处理
     *
     * @param e ServiceException实例，包含错误代码和消息
     * @return 包含错误信息的响应实体
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(ServiceException e) {
        // 创建一个HashMap来存储错误响应信息
        Map<String, Object> errorResponse = new HashMap<>();
        // 将异常的错误代码和消息放入错误响应中
        errorResponse.put("code", e.getCode());
        errorResponse.put("message", e.getMessage());
        // 返回包含错误信息的响应实体，状态码为异常指定的代码
        return ResponseEntity.status(e.getCode()).body(errorResponse);
    }

    /**
     * 处理通用异常
     * 当抛出非ServiceException的其他异常时，此方法将被捕获并处理
     *
     * @param e Exception实例，包含异常消息
     * @return 包含错误信息的响应实体
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        // 创建一个HashMap来存储错误响应信息
        Map<String, Object> errorResponse = new HashMap<>();
        // 默认错误代码为500，表示服务器内部错误
        errorResponse.put("code", 500);
        // 将异常消息格式化后放入错误响应中
        errorResponse.put("message", "服务器内部错误: " + e.getMessage());
        // 返回包含错误信息的响应实体，状态码为HTTP 500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
