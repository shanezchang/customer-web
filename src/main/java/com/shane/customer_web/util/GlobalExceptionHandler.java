package com.shane.customer_web.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public R<?> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常 [{}] {} - {}",
                e.getCode(), e.getMessage(), request.getRequestURI());
        return R.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public R<?> handleGlobalException(Exception e) {
        log.error("全局异常 {}", e.getMessage());
        return R.fail(RspCode.INNER_ERROR.getCode(), RspCode.INNER_ERROR.getMessage());
    }
}