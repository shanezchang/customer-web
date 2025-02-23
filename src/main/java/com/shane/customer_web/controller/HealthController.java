package com.shane.customer_web.controller;

import com.shane.customer_web.util.BusinessException;
import com.shane.customer_web.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HealthController {

    @GetMapping("/health")
    public R<Object> health() {
        log.info("invoke /health.");
        return R.success();
    }

    @GetMapping("/test")
    public R<Object> test() {
        log.info("invoke /test.");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new BusinessException(1001, e.getMessage());
        }
        throw new BusinessException(1001, "test");
    }

    @GetMapping("/test2")
    public R<Object> test2() {
        log.info("invoke /test2.");
        // 休眠6秒
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            throw new BusinessException(1001, e.getMessage());
        }
        return R.success();
    }
}
