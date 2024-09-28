package com.tsh.slt.service.messageComm.systemTest.vo;


import com.tsh.slt.spec.SrvMsgComSlcSendIvo;
import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;

@Data
public class SlcMessageSendJobVo {

    SrvMsgComSlcSendIvo srvMsgComSlcSendIvo;

    /* 작업 시작 및 종료 시간 */
    Long jobStartTm;
    Long jobEndTm;

    /* 현재 Thread 기준 진행 중인 개수 */
    ConcurrentHashMap<String, CrntCountVo> threadCrntMap;

}



class CrntCountVo {
    Long crntLoopCnt;
    Long crntUnitCnt;
}
