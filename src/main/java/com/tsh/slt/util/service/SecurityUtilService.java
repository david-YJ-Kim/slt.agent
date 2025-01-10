package com.tsh.slt.util.service;


import com.tsh.slt.util.service.vo.SecurityInfoVo;
import com.tsh.slt.util.service.vo.SecurityValidateReqVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class SecurityUtilService {


    /**
     * 비밀번호 저장을 위해 암호화된 비밀번호 저장
     * @param userId
     * @param userPwd
     * @return
     */
    public SecurityInfoVo generateSecurityInfo(String userId, String userPwd) {

        String salt = this.generateSaltInfo();

        return SecurityInfoVo.builder()
                .userId(userId)
                .salt(salt)
                .hashedPwd(this.generateHashPwd(userPwd, salt))
                .build();

    }

    /**
     * 유저의 비밀번호가 저장된 것과 맞는지 확인해주는 메소드
     * @param vo
     * @return
     */
    public boolean validatePwd(SecurityValidateReqVo vo){

        // 비밀번호 검증
        boolean isPwdMatched = BCrypt.checkpw(vo.getUserPwd() + vo.getSalt(), vo.getHashedPwd());
        log.info("isPwdMached:{}", isPwdMatched);

        return isPwdMatched;
    }


    /**
     * Salt 를 이용하여 해싱된 비밀번호 생성
     * @param userPwd
     * @param salt
     * @return
     */
    private String generateHashPwd(String userPwd, String salt){
        String hashedPwd = BCrypt.hashpw(userPwd + salt, BCrypt.gensalt());
        return hashedPwd;
    }

    /**
     * SALT 정보 생성
     * @return
     */
    private String generateSaltInfo(){
        return UUID.randomUUID().toString();
    }

    public static void main(String[] args) {
        // 사용자가 요청한 계정 비밀번호
        String plainPwd = "myPwd";

        // Salt 생성
        String salt = UUID.randomUUID().toString();

        // 비밀번호 해싱
        String hashedPwd = BCrypt.hashpw(plainPwd + salt, BCrypt.gensalt());

        // 저장할 값 출력
        log.info("Salt: {}, HashedPwd: {}",salt, hashedPwd);

        // 비밀번호 검증
        boolean isPwdMatched = BCrypt.checkpw(plainPwd + salt, hashedPwd);
        log.info("isPwdMached:{}", isPwdMatched);

    }


}
