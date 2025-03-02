package com.shane.customer_web.util;

import lombok.Getter;

@Getter
public enum RspCode {
    SUCCESS(200, "success"),
    INNER_ERROR(500, "系统繁忙，请稍后再试"),

    USER_NOT_FOUND(1001, "用户不存在"),
    INVALID_PARAM(1002, "参数无效"),
    DUPLICATE_ENTRY(1003, "重复数据"),
    SIGN_IN_TIMEOUT(1004, "登录超时"),
    SIGN_IN_PASSWORD_ERROR(1005, "登录密码错误"),
    TOKEN_ERROR(1006, "token无效"),

    PARAM_ERROR(2001, "参数错误"),

    EMAIL_ADDRESS_ERROR(3001, "邮箱地址错误"),
    EMAIL_FREQUENT_ERROR(3002, "邮件发送频繁"),
    VERIFY_CODE_ERROR(3003, "验证码错误"),
    ;

    private final int code;
    private final String message;

    RspCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}