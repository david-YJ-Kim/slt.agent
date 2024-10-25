package com.tsh.slt.spec;

import com.tsh.slt.interfaces.util.ApMessageList;
import com.tsh.slt.service.httpRequest.vo.HttpRequestVo;
import com.tsh.slt.spec.common.AbsMsgCommonVo;
import com.tsh.slt.spec.common.ApMsgBody;
import lombok.Data;

@Data
public class SrvMsgComHttpSendIvo extends AbsMsgCommonVo {

    public static String cid = ApMessageList.SRV_MSG_COM_HTTP_SEND;

    Body body;

    @Data
    public static class Body extends SrvMsgComSlcSendIvo.Body{

        HttpRequestVo httpRequestVo;


    }

}
