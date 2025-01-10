package com.tsh.slt.util.service.vo;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@SuperBuilder
public class SecurityInfoVo {

    String userId;
    String salt;
    String hashedPwd;
}
