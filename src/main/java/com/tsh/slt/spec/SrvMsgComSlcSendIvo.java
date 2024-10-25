package com.tsh.slt.spec;

import com.tsh.slt.interfaces.util.ApMessageList;
import com.tsh.slt.service.httpRequest.vo.HttpRequestVo;
import com.tsh.slt.spec.common.AbsMsgCommonVo;
import com.tsh.slt.spec.common.ApMsgBody;
import com.tsh.slt.spec.common.ApMsgCommonVo;
import lombok.Data;

@Data
public class  SrvMsgComSlcSendIvo extends AbsMsgCommonVo {

    public static String cid = ApMessageList.SRV_MSG_COM_SLC_SEND;

    Body body;

    @Data
    public static class Body extends ApMsgBody{
        //        String env;             // 시스템 환경 (PROD / DEV)
        String systemNm;        // 테스트 시스템 명
        String testCd;          // 테스트 코드
        String sendTopicInfo;   // 토픽 발생 정보
        // sample: EQP(1-10),CMN(1-10)

        int bizExecuteCnt;      // 비즈로직 수행 횟수
        int retentionSecond;    // 유지시간 (초)
        int targetTps;          // 1초당 발송해야하는 메시지 개수


        HttpRequestVo httpRequestVo; // http 메시지 샌드 인포

    }

}

