package com.tsh.slt.spec;

import com.tsh.slt.interfaces.util.ApMessageList;
import com.tsh.slt.spec.common.AbsMsgCommonVo;
import com.tsh.slt.spec.common.ApMsgBody;
import com.tsh.slt.spec.common.ApMsgCommonVo;
import lombok.Data;

@Data
public class ApSysTestIvo extends AbsMsgCommonVo {

    public static String cid = ApMessageList.AP_SYS_TEST;

    Body body;

    @Data
    public static class Body extends ApMsgBody{

        String testCd;        // 테스트 코드
        String recvTopicNm;   // 발송하는 토픽 명
        String msgSendDt;     // 메시지 발송 시간
        Long loopMsgCnt;      // loop 횟수
        Long unitMsgCnt;      // unit 횟수

    }

}
