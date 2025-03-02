package com.shane.customer_web.service;

import com.shane.customer_web.mapper.IUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final IUserMapper userMapper;

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
