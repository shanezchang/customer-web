package com.shane.customer_web.controller;

import com.shane.customer_web.model.bo.SendCodeBO;
import com.shane.customer_web.model.bo.SignInBO;
import com.shane.customer_web.model.bo.SignUpBO;
import com.shane.customer_web.service.IUserService;
import com.shane.customer_web.util.R;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final IUserService userService;

    @PostMapping("/sign_in")
    public R<String> signIn(@RequestBody @Validated SignInBO param) {
        log.info("invoke /user/sign_in. param:{}", param);
        return R.success(userService.signIn(param.getEmail(), param.getSignature()));
    }

    @PostMapping("/sign_up")
    public R<Void> signUp(@RequestBody @Validated SignUpBO param) {
        log.info("invoke /user/sign_up. param:{}", param);
        userService.signUp(param.getEmail(), param.getPassword(), param.getVerifyCode());
        return R.success();
    }

    @PostMapping("/send_code")
    public R<Void> sendCode(@RequestBody @Validated SendCodeBO param) {
        log.info("invoke /user/send_code. param:{}", param);
        userService.sendCode(param.getEmail());
        return R.success();
    }

    @GetMapping("/auth_token")
    public R<Long> authToken(@RequestParam @NotBlank String token) {
        log.info("invoke /user/auth_token. token:{}", token);
        return R.success(userService.authToken(token));
    }

    @GetMapping("/get_password_md5")
    public R<String> getPasswordMd5(@RequestParam @NotBlank String password) {
        log.info("invoke /user/get_password_md5. password:{}", password);
        return R.success(userService.getPasswordMd5(password));
    }

    @GetMapping("/gen_signature")
    public R<String> genSignature(@RequestParam @NotBlank String password) {
        log.info("invoke /user/gen_signature. password:{}", password);
        return R.success(userService.genSignature(password));
    }

}
