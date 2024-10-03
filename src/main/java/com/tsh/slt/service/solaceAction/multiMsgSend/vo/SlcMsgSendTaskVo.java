package com.tsh.slt.service.solaceAction.multiMsgSend.vo;


import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;

@Data
@Builder
public class SlcMsgSendTaskVo {

    String topicName;
    String testCd;
    String targetSys;
    ScheduledExecutorService service;
    ArrayList<String> runningThreadList;
    String myThreadName;
    int bizExecuteCnt;                      // 비즈로직 수행 횟수
    int retentionSecond;
    int targetTps;
}
