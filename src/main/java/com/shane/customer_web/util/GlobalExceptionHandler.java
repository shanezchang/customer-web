package com.shane.customer_web.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolationException;

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

    @ExceptionHandler(value = BindException.class)
    public R<?> exceptionHandler(BindException e) {
        String errMsg = e.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return String.format("%s: %s", fieldError.getField(), error.getDefaultMessage());
                    }
                    return error.getDefaultMessage();
                }).collect(Collectors.joining("; "));

        errMsg = errMsg.isEmpty() ? RspCode.PARAM_ERROR.getMessage() : errMsg;
        log.warn("参数校验失败 {}", errMsg);
        return R.fail(RspCode.PARAM_ERROR.getCode(), errMsg);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public R<?> handleConstraintViolation(ConstraintViolationException e) {
        String errMsg = e.getConstraintViolations().stream()
                .map(v -> {
                    // 更安全的字段名提取方式
                    String[] pathNodes = v.getPropertyPath().toString().split("\\.");
                    String fieldName = pathNodes[pathNodes.length - 1];
                    return String.format("%s: %s", fieldName, v.getMessage());
                })
                .collect(Collectors.joining("; "));

        log.warn("参数校验失败（GET） {}", errMsg);
        return R.fail(RspCode.PARAM_ERROR.getCode(), errMsg);
    }
}