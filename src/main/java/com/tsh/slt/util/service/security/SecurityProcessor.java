package com.tsh.slt.util.service.security;

import com.tsh.slt.util.service.vo.SecurityInfoVo;
import com.tsh.slt.util.service.vo.SecurityValidateReqVo;

// 암호화 처리 인터페이스
public interface SecurityProcessor {
    SecurityInfoVo encrypt(String userId, String plainText);
    boolean validate(SecurityValidateReqVo vo);
}