package com.shane.customer_web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shane.customer_web.mapper.IUserMapper;
import com.shane.customer_web.model.dto.UserTokenDTO;
import com.shane.customer_web.model.entity.UserEntity;
import com.shane.customer_web.service.IUserService;
import com.shane.customer_web.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final SnowflakeIdGenerator snowflakeIdGenerator;

    private final AesPasswordEncryptor aesPasswordEncryptor;

    private final IUserMapper userMapper;

    private final MailService mailService;

    @Value("${sign-in.valid-minute}")
    private Long signInValidMinute;

    @Value("${sign-in.expire-day}")
    private Long signInExpireDay;

    private final String SIGN_IN_TOKEN_REDIS_KEY_PREFIX = "SIGN_IN_TOKEN:";
    private final String SEND_EMAIL_REDIS_KEY_PREFIX = "SEND_EMAIL:";
    private final String SIGN_UP_EMAIL_REDIS_KEY_PREFIX = "SIGN_UP_EMAIL:";
    private final String VERIFY_CODE_REDIS_KEY_PREFIX = "VERIFY_CODE:";

    @Override
    public String signIn(String email, String signature) {
        /*
        0. 校验 email 是否存在
        1. 解析 signature 获取 时间戳和密码 并执行校验
        2. 登录成功 返回 token 并存储到 redis 中
         */
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getEmail, email);
        UserEntity entity = userMapper.selectOne(queryWrapper);
        if (entity == null) {
            throw new BusinessException(RspCode.USER_NOT_FOUND);
        }

        AesPasswordEncryptor.DecryptedResult decryptedResult = aesPasswordEncryptor.aesDecrypt(signature);
        log.info("decryptedResult:{}", decryptedResult);
        decryptedResult.isValid(signInValidMinute, entity.getPassword(), signInExpireDay);

        // 登录成功 返回 token 并存储到 redis 中
        long currentTime = System.currentTimeMillis();
        UserTokenDTO userTokenDTO = UserTokenDTO.builder()
                .userId(entity.getUserId())
                .expireTimestamp(decryptedResult.getExpireTimestamp())
                .build();
        String token = Md5Encryptor.encrypt(entity.getUserId() + String.valueOf(currentTime));
        log.info("token:{}", token);
        // 存储到 redis 中, 并指定过期的时间戳 计算剩余存活时间
        redisTemplate.opsForValue().set(
                SIGN_IN_TOKEN_REDIS_KEY_PREFIX + token,
                userTokenDTO,
                Duration.ofMillis(decryptedResult.getExpireTimestamp() - currentTime)
        );
        return token;
    }

    @Override
    public void signUp(String email, String password, String verifyCode) {
        // 使用redis 实现以email为key的自旋锁
        String key = SIGN_UP_EMAIL_REDIS_KEY_PREFIX + email;
        if (Boolean.FALSE.equals(redisTemplate.opsForValue().setIfAbsent(key,
                "1", Duration.ofSeconds(60)))) {
            throw new BusinessException(RspCode.EMAIL_FREQUENT_ERROR);
        }
        try {
            // 查询redis中是否有此邮箱的验证码
            if (!verifyCode.equals(redisTemplate.opsForValue().get(VERIFY_CODE_REDIS_KEY_PREFIX + email))) {
                throw new BusinessException(RspCode.VERIFY_CODE_ERROR);
            }
            // 查询之前没有已有账号
            LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserEntity::getEmail, email);
            UserEntity entity = userMapper.selectOne(queryWrapper);
            if (entity != null) {
                throw new BusinessException(RspCode.DUPLICATE_ENTRY);
            }
            // 验证码通过验证 清理此redis缓存
            redisTemplate.delete(key);
            // 执行账号注册逻辑
            long currentTimestamp = System.currentTimeMillis();
            userMapper.insert(UserEntity.builder()
                    .userId(snowflakeIdGenerator.nextId())
                    .nickname(email)
                    .email(email)
                    .password(password)
                    .createTimestamp(currentTimestamp)
                    .updateTimestamp(currentTimestamp)
                    .build());
        } finally {
            redisTemplate.delete(key);
        }
    }

    @Override
    public void register(String email, String nickname, String password) {
    }

    @Override
    public void sendCode(String email) {
        String key = SEND_EMAIL_REDIS_KEY_PREFIX + email;
        // redis实现一分钟的分布式锁
        if (Boolean.FALSE.equals(redisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofSeconds(60)))) {
            throw new BusinessException(RspCode.EMAIL_FREQUENT_ERROR);
        }
        // 查询之前没有已有账号
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getEmail, email);
        UserEntity entity = userMapper.selectOne(queryWrapper);
        if (entity != null) {
            throw new BusinessException(RspCode.DUPLICATE_ENTRY);
        }
        int code = ThreadLocalRandom.current().nextInt(100000, 1000000);
        mailService.sendEmail(email, "验证码", String.valueOf(code));
        // 发送成功之后 用redis存下来验证码用于后续的校验
        redisTemplate.opsForValue().set(VERIFY_CODE_REDIS_KEY_PREFIX + email,
                String.valueOf(code), Duration.ofMinutes(5));
    }

    @Override
    public Long authToken(String token) {
        UserTokenDTO user = (UserTokenDTO) redisTemplate.opsForValue().get(SIGN_IN_TOKEN_REDIS_KEY_PREFIX + token);
        if (user == null) {
            throw new BusinessException(RspCode.TOKEN_ERROR);
        }
        // 验证token是否过期
        if (user.getExpireTimestamp() < System.currentTimeMillis()) {
            throw new BusinessException(RspCode.TOKEN_ERROR);
        }
        return user.getUserId();
    }

    @Override
    public String getPasswordMd5(String password) {
        return Md5Encryptor.encrypt(password);
    }

    @Override
    public String genSignature(String password) {
        return aesPasswordEncryptor.aesEncrypt(password, System.currentTimeMillis());
    }
}
