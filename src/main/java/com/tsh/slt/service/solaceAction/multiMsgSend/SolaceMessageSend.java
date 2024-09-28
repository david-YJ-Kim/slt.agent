package com.tsh.slt.service.solaceAction.multiMsgSend;


import com.tsh.slt.data.ApSharedVariable;
import com.tsh.slt.interfaces.solace.InterfaceSolacePub;
import com.tsh.slt.service.solaceAction.multiMsgSend.vo.SlcMessageSendJobVo;
import com.tsh.slt.spec.SrvMsgComSlcSendIvo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@Slf4j
public class SolaceMessageSend {

    ApSharedVariable sharedVariable = ApSharedVariable.getInstance();


    @Async
    public void executeJob(SrvMsgComSlcSendIvo ivo){


        SrvMsgComSlcSendIvo.Body body = ivo.getBody();

        ArrayList<String> sendTopicList = SolaceActionUtil.getSendTopicList(body.getSystemNm(), body.getSendTopicInfo());

        log.info("Target send topic list: {}", sendTopicList);

        SlcMessageSendJobVo jobVo = new SlcMessageSendJobVo();
        jobVo.setJobStartTm(System.currentTimeMillis());
        jobVo.setSrvMsgComSlcSendIvo(ivo);



        ArrayList<String> runningThreadList = new ArrayList<>();

        ScheduledExecutorService executorService = this.createScheduledExecutorService(Math.toIntExact(body.getSenderCtn()));
        for(int i=0; i < body.getSenderCtn(); i++){

            String threadName = String.format("%s-%s", Thread.currentThread().getName(), String.valueOf(i + 1));
            log.info("Job Start {}", threadName);

            Runnable task = this.taskEveryOneSecond(executorService, runningThreadList,
                    threadName, body.getRetentionSecond(), body.getTargetTps());

            executorService.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);


        }
    }


    /**
     * 생성하기
     * @param senderCnt
     * @return
     */
    private ScheduledExecutorService createScheduledExecutorService(int senderCnt){
        return Executors.newScheduledThreadPool(senderCnt);

    }


    /**
     * 메시지 발송하는 실제 Task 메소드
     * 1초 마다 진행
     * @param retentionSecond
     * @param targetTps
     * @return
     */
    private Runnable taskEveryOneSecond(ScheduledExecutorService service, ArrayList<String> runningThreadList,
                                            String myThreadName, int retentionSecond, int targetTps){

        AtomicInteger invokeTime = new AtomicInteger(0);
        Long sleepMs = this.calculateDelayTimeWithTPS(targetTps);

        return () -> {

            invokeTime.getAndIncrement();

            if(retentionSecond + 1 == invokeTime.get()){


                runningThreadList.remove(myThreadName);
                log.info("Complete run.! invoke: {}, retention: {}", invokeTime, retentionSecond);

                if(runningThreadList.isEmpty()){
                    log.info("This is last thread. shut down service.");
                    service.shutdown();
                }
            }


            int unitMsgCnt = 0;

            while (true){
                unitMsgCnt ++;
                if(unitMsgCnt == targetTps + 1){ break;}

                log.info("Run Thread. name: {}, invokeTime: {} unitMsgCnt: {}", myThreadName, invokeTime, unitMsgCnt);

                try {
                    Thread.sleep(sleepMs);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        };
    }


    /**
     * 1초 마다, 목표 TPS에 맞는 Sleep 시간을 리턴
     * 단위: 1초
     * @param targetTps
     * @return
     */
    private Long calculateDelayTimeWithTPS(int targetTps){

        if (targetTps <= 0) {
            throw new IllegalArgumentException("targetTps must be greater than 0");
        }

        // 1초(1000ms)를 targetTps로 나누어 각 메시지 사이의 sleep 시간을 계산
        return 1000L / targetTps;
    }


}
