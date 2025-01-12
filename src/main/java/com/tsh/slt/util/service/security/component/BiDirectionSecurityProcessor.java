package com.tsh.slt.util.service.security.component;


import com.tsh.slt.util.service.security.SecurityProcessor;
import com.tsh.slt.util.service.vo.SecurityInfoVo;
import com.tsh.slt.util.service.vo.SecurityValidateReqVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

// 양방향 암호화 구현체
@Component
@Slf4j
public class BiDirectionSecurityProcessor implements SecurityProcessor {

    @Value("${encryption.key}")
    private String encryptionKey;

    private static final String ALGORITHM = "AES";

    @Override
    public SecurityInfoVo encrypt(String userId, String plainText) {
        try {
            SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);

            return SecurityInfoVo.builder()
                    .userId(userId)
                    .hashedPwd(encryptedText)
                    .build();
        } catch (Exception e) {
            log.error("Error in encryption", e);
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public String decrypt(String encryptedText) {
        try {
            SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decryptedBytes);
        } catch (Exception e) {
            log.error("Error in decryption", e);
            throw new RuntimeException("Decryption failed", e);
        }
    }

    @Override
    public boolean validate(SecurityValidateReqVo vo) {
        try {
            String decryptedPwd = decrypt(vo.getHashedPwd());
            return decryptedPwd.equals(vo.getUserPwd());
        } catch (Exception e) {
            log.error("Error in validation", e);
            return false;
        }
    }
}