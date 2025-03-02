package com.shane.customer_web.controller;

import com.shane.customer_web.model.bo.SignInBO;
import com.shane.customer_web.service.IUserService;
import com.shane.customer_web.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final IUserService userService;

    // sign in
    @PostMapping("/sign_in")
    public R<?> signIn(@RequestBody @Validated SignInBO param) {
        log.info("invoke /user/sign_in. param:{}", param);
        return R.success();
    }

}
