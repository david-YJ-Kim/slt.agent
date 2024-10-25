package com.tsh.slt.service.solaceAction.multiMsgSend.vo;


import com.tsh.slt.service.httpRequest.vo.HttpRequestVo;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;

@Data
@Builder
public class MsgSendTaskVo {

    String topicName;
    String testCd;
    String targetSys;
    ScheduledExecutorService service;
    ArrayList<String> runningThreadList;
    String myThreadName;
    int bizExecuteCnt;                      // 비즈로직 수행 횟수
    int retentionSecond;
    int targetTps;


    HttpRequestVo httpRequestVo; // http 발송용
}
