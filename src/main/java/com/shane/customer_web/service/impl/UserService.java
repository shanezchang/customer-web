package com.shane.customer_web.service.impl;

import com.shane.customer_web.mapper.IUserMapper;
import com.shane.customer_web.service.IUserService;
import com.shane.customer_web.util.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final SnowflakeIdGenerator snowflakeIdGenerator;

    private final IUserMapper userMapper;

    @Override
    public void signIn(String account, String password) {

        /*
        0. 校验 account 是否存在
        1. 解析 signature 获取 时间戳和密码
        2. 校验时间戳是否超时
        3. 校验密码是否正确
        4. 登录成功 返回 token 并存储到 redis 中
         */

        /*
        网关验证
        1. 校验 token 是否存在
        2. 通过 token 获取用户信息
        3. 将 用户信息作为 header 请求业务服务
         */

    }

    @Override
    public void register(String email, String nickname, String password) {
        // 加自旋锁 校验 email 是否已有账号

        if (Strings.isBlank(nickname)) {
            nickname = email;
        }
        // 根据日期生成单号 自增设计

        long currentTime = System.currentTimeMillis();
        log.info(String.valueOf(currentTime));
        userMapper.selectById(1);
//        userMapper.insert(UserEntity.builder()
//                .account()
//                .nickname(nickname)
//                .email(email)
//                .password(password)
//                .createTimestamp(currentTime)
//                .updateTimestamp(currentTime)
//                .build());
    }
}
