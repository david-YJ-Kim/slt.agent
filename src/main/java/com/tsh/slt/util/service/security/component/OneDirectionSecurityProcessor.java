package com.tsh.slt.util.service.security.component;


import com.tsh.slt.util.service.security.SecurityProcessor;
import com.tsh.slt.util.service.vo.SecurityInfoVo;
import com.tsh.slt.util.service.vo.SecurityValidateReqVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.util.UUID;

// 단방향 암호화 구현체
@Component
@Slf4j
public class OneDirectionSecurityProcessor implements SecurityProcessor {
    @Override
    public SecurityInfoVo encrypt(String userId, String plainText) {
        String salt = UUID.randomUUID().toString();
        return SecurityInfoVo.builder()
                .userId(userId)
                .salt(salt)
                .hashedPwd(BCrypt.hashpw(plainText + salt, BCrypt.gensalt()))
                .build();
    }

    @Override
    public boolean validate(SecurityValidateReqVo vo) {
        try {
            return BCrypt.checkpw(vo.getUserPwd() + vo.getSalt(), vo.getHashedPwd());
        } catch (Exception e) {
            log.error("Error in password validation", e);
            return false;
        }
    }
}