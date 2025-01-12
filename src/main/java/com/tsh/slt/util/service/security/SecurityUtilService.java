package com.tsh.slt.util.service.security;


import com.tsh.slt.util.service.security.component.BiDirectionSecurityProcessor;
import com.tsh.slt.util.service.security.component.OneDirectionSecurityProcessor;
import com.tsh.slt.util.service.vo.SecurityInfoVo;
import com.tsh.slt.util.service.vo.SecurityValidateReqVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.UUID;

// 수정된 SecurityUtilService
@Service
@Slf4j
public class SecurityUtilService {
    @Autowired
    private OneDirectionSecurityProcessor oneDirectionProcessor;

    @Autowired
    private BiDirectionSecurityProcessor biDirectionProcessor;

    public SecurityInfoVo generateSecurityInfo(String userId, String plainText, boolean isBiDirectional) {
        SecurityProcessor processor = isBiDirectional ? biDirectionProcessor : oneDirectionProcessor;
        return processor.encrypt(userId, plainText);
    }

    public boolean validateSecurityInfo(SecurityValidateReqVo vo, boolean isBiDirectional) {
        SecurityProcessor processor = isBiDirectional ? biDirectionProcessor : oneDirectionProcessor;
        return processor.validate(vo);
    }

    public String decryptBiDirectional(String encryptedText) {
        return biDirectionProcessor.decrypt(encryptedText);
    }
}