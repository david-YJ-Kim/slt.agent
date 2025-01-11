package com.tsh.slt.spec;

import com.tsh.slt.interfaces.util.ApMessageList;
import com.tsh.slt.service.httpRequest.vo.HttpRequestVo;
import com.tsh.slt.spec.common.AbsMsgCommonVo;
import com.tsh.slt.spec.common.ApMsgBody;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class SrvMsgComHttpSendIvo extends AbsMsgCommonVo {

    public static String cid = ApMessageList.SRV_MSG_COM_HTTP_SEND;

    Body body;

    @Getter
    @Setter
    @SuperBuilder
    public static class Body extends SrvMsgComSlcSendIvo.Body{

        HttpRequestVo httpRequestVo;


    }

}
