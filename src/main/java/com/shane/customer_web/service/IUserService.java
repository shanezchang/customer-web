package com.shane.customer_web.service;

import jakarta.validation.constraints.NotBlank;

public interface IUserService {

    String signIn(String email, String signature);

    void signUp(String email, String password, String verifyCode);

    // 注册账号
    void register(String email, String nickname, String password);

    void sendCode(@NotBlank String email);

    Long authToken(String token);
}
