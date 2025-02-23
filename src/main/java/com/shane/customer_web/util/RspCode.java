package com.shane.customer_web.util;

import lombok.Getter;

@Getter
public enum RspCode {
    SUCCESS(200, "success"),
    INNER_ERROR(500, "系统繁忙，请稍后再试"),

    USER_NOT_FOUND(1001, "用户不存在"),
    INVALID_PARAM(1002, "参数无效"),
    DUPLICATE_ENTRY(1003, "重复数据"),
    ;

    private final int code;
    private final String message;

    RspCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}