package com.gec.common;

import lombok.Data;

/**
 * 通用响应结果类
 */
@Data
public class R<T> {
    
    private Integer code;
    private String message;
    private T data;
    
    public R() {}
    
    public R(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public R(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    public static <T> R<T> success() {
        return new R<>(200, "操作成功");
    }
    
    public static <T> R<T> success(T data) {
        return new R<>(200, "操作成功", data);
    }
    
    public static <T> R<T> success(String message, T data) {
        return new R<>(200, message, data);
    }
    
    public static <T> R<T> error() {
        return new R<>(500, "操作失败");
    }
    
    public static <T> R<T> error(String message) {
        return new R<>(500, message);
    }
    
    public static <T> R<T> error(Integer code, String message) {
        return new R<>(code, message);
    }
}
