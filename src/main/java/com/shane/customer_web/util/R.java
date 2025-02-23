package com.shane.customer_web.util;

import lombok.Getter;

@Getter
public class R<T> {
    private final int code;
    private final String msg;
    private final T data;
    private final long timestamp;

    // 构造方法
    private R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    // 成功响应（无数据）
    public static <T> R<T> success() {
        return new R<>(RspCode.SUCCESS.getCode(), RspCode.SUCCESS.getMessage(), null);
    }

    // 成功响应（带数据）
    public static <T> R<T> success(T data) {
        return new R<>(RspCode.SUCCESS.getCode(), RspCode.SUCCESS.getMessage(), data);
    }

    // 失败响应
    public static <T> R<T> fail(int code, String msg) {
        return new R<>(code, msg, null);
    }

}
