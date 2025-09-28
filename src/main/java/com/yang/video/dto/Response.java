package com.yang.video.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 标准响应格式
 * @param <T> 响应数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {
    /**
     * 响应状态码
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 成功响应
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功响应
     */
    public static <T> Response<T> success(T data) {
        return new Response<>(200, "操作成功", data, true);
    }
    
    /**
     * 成功响应（无数据）
     * @return 成功响应
     */
    public static <T> Response<T> success() {
        return new Response<>(200, "操作成功", null, true);
    }
    
    /**
     * 成功响应（自定义消息）
     * @param message 响应消息
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功响应
     */
    public static <T> Response<T> success(String message, T data) {
        return new Response<>(200, message, data, true);
    }
    
    /**
     * 失败响应
     * @param code 错误码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 失败响应
     */
    public static <T> Response<T> error(Integer code, String message) {
        return new Response<>(code, message, null, false);
    }
    
    /**
     * 失败响应（默认500错误）
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 失败响应
     */
    public static <T> Response<T> error(String message) {
        return new Response<>(500, message, null, false);
    }
}
