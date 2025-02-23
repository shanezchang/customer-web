package com.shane.customer_web.util;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    // 通过枚举构造
    public BusinessException(RspCode rspCode) {
        super(rspCode.getMessage());
        this.code = rspCode.getCode();
    }

    // 允许覆盖消息
    public BusinessException(int code, String msg) {
        super(msg);
        this.code = code;
    }

}