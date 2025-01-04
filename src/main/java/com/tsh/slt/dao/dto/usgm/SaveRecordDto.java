package com.tsh.slt.dao.dto.usgm;

import com.tsh.slt.interfaces.util.ApMessageList;
import lombok.Data;

@Data
public class SaveRecordDto {

    public static String cid = ApMessageList.SRV_USGM_NEW_RECORD;

    String lclRpNm;
    String lclRpPth;
    String rmtRpUrl;
    String rmtRpBrnNm;
    String email;
    String pwdHsh;
    String salt;
    String userId;

}
