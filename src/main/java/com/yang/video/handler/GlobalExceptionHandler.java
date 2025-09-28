package com.yang.video.handler;

import com.yang.video.dto.Response;
import com.yang.video.exception.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
    public ResponseEntity<Response<Object>> handleBusinessException(ServiceException e) {
        // 返回标准格式的错误响应
        return ResponseEntity.status(e.getCode()).body(Response.error(e.getCode(), e.getMessage()));
    }

    /**
     * 处理通用异常
     * 当抛出非ServiceException的其他异常时，此方法将被捕获并处理
     *
     * @param e Exception实例，包含异常消息
     * @return 包含错误信息的响应实体
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Object>> handleGenericException(Exception e) {
        // 返回标准格式的错误响应
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(500, "服务器内部错误: " + e.getMessage()));
    }
}
