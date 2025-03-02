package com.shane.customer_web.service;

public interface IUserService {

    void signIn(String account, String password);

    // 注册账号
    void register(String email, String nickname, String password);
}
