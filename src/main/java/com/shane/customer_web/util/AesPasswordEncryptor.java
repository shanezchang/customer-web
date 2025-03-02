package com.shane.customer_web.util;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Component
public class AesPasswordEncryptor {

    // 算法参数
    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";
    // GCM 认证标签长度
    private static final int GCM_TAG_LENGTH = 128;
    // GCM 推荐 12 字节 IV
    private static final int IV_LENGTH = 12;

    private final SecretKey secretKey;

    @Autowired
    public AesPasswordEncryptor(@Value("${aes.secret}") String aesSecret) {
        this.secretKey = new SecretKeySpec(aesSecret.getBytes(StandardCharsets.UTF_8), "AES");
    }


    /**
     * 加密方法
     *
     * @param password  原始密码
     * @param timestamp 时间戳
     * @return Base64(IV + AES密文)
     */
    public String aesEncrypt(String password, long timestamp) {
        try {
            // 1. 生成密码的 MD5 哈希
            byte[] passwordMd5 = MessageDigest.getInstance("MD5")
                    .digest(password.getBytes(StandardCharsets.UTF_8));

            // 2. 构造明文数据：MD5 + 时间戳
            byte[] timestampBytes = ByteBuffer.allocate(8).putLong(timestamp).array();
            byte[] plaintext = ByteBuffer.allocate(passwordMd5.length + timestampBytes.length)
                    .put(passwordMd5)
                    .put(timestampBytes)
                    .array();

            // 3. 生成随机 IV
            byte[] iv = new byte[IV_LENGTH];
            SecureRandom.getInstanceStrong().nextBytes(iv);

            // 4. AES-GCM 加密
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
            byte[] ciphertext = cipher.doFinal(plaintext);

            // 5. 组合 IV + 密文，Base64 编码
            byte[] encryptedData = ByteBuffer.allocate(iv.length + ciphertext.length)
                    .put(iv)
                    .put(ciphertext)
                    .array();
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            log.error("加密失败", e);
            throw new BusinessException(RspCode.INNER_ERROR);
        }
    }

    /**
     * 解密方法
     *
     * @param encryptedData Base64(IV + AES密文)
     * @return 解密后的 MD5 和时间戳
     */
    public DecryptedResult aesDecrypt(String encryptedData) {
        try {
            // 1. Base64 解码
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);

            // 2. 分离 IV 和密文
            ByteBuffer buffer = ByteBuffer.wrap(encryptedBytes);
            byte[] iv = new byte[IV_LENGTH];
            buffer.get(iv);
            byte[] ciphertext = new byte[buffer.remaining()];
            buffer.get(ciphertext);

            // 3. AES-GCM 解密
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);
            byte[] plaintext = cipher.doFinal(ciphertext);

            // 4. 分离 MD5 和时间戳
            ByteBuffer plainBuffer = ByteBuffer.wrap(plaintext);
            // MD5 为 16 字节
            byte[] passwordMd5 = new byte[16];
            plainBuffer.get(passwordMd5);
            long timestamp = plainBuffer.getLong();
            return DecryptedResult.builder()
                    .passwordMd5(bytesToHex(passwordMd5))
                    .signInTimestamp(timestamp)
                    .build();
        } catch (Exception e) {
            log.error("解密失败", e);
            throw new BusinessException(RspCode.INNER_ERROR);
        }
    }

    // 辅助方法：字节数组转十六进制
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // 解密结果包装类
    @Data
    @Builder
    public static class DecryptedResult {
        private final String passwordMd5;
        private final Long signInTimestamp;
        private Long expireTimestamp;

        // 可添加验证方法，例如检查时间戳是否在合理范围内
        public void isValid(Long validMinute, String password, Long expireDay) {
            this.expireTimestamp = signInTimestamp + expireDay * 24 * 60 * 60 * 1000;
            if (signInTimestamp + validMinute * 60 * 1000 < System.currentTimeMillis()) {
                throw new BusinessException(RspCode.SIGN_IN_TIMEOUT);
            }
            if (!password.equals(passwordMd5)) {
                throw new BusinessException(RspCode.SIGN_IN_PASSWORD_ERROR);
            }
        }
    }

}

