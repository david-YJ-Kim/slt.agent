package com.tsh.slt.service.solaceAction.multiMsgSend;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solacesystems.jcsmp.JCSMPException;
import com.tsh.slt.data.ApSharedVariable;
import com.tsh.slt.interfaces.solace.InterfaceSolacePub;
import com.tsh.slt.service.httpRequest.HttpRequestService;
import com.tsh.slt.service.httpRequest.vo.HttpRequestVo;
import com.tsh.slt.service.solaceAction.multiMsgSend.vo.FisMsgSendReqVo;
import com.tsh.slt.service.solaceAction.multiMsgSend.vo.MsgSendTaskVo;
import com.tsh.slt.service.solaceAction.multiMsgSend.vo.SlcMessageSendJobVo;
import com.tsh.slt.spec.ApSysTestIvo;
import com.tsh.slt.spec.FisFileReportVo;
import com.tsh.slt.spec.SrvMsgComSlcSendIvo;
import com.tsh.slt.spec.common.AbsMsgHead;
import com.tsh.slt.util.ApCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@Slf4j
public class MessageSendService {

    public static final String threadNameFormat = "%s-%s";
    public static final String cidNameFormat = "%s_AP_SYS_TEST";
    public static final String tidFormat = "%s-%s-%s-%s";
    public static final String testCdFormat = "%s-%s";
    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    ApSharedVariable sharedVariable = ApSharedVariable.getInstance();


    @Autowired
    HttpRequestService httpRequestService;

    @Async
    public void executeJob(SrvMsgComSlcSendIvo ivo){


        SrvMsgComSlcSendIvo.Body body = ivo.getBody();

        ArrayList<String> sendTopicList = SolaceActionUtil.getSendTopicList(body.getSystemNm(), body.getSendTopicInfo());

        log.info("Target send topic list: {}", sendTopicList);

        // TODO Job 을 관리하는 로직 필요 시
        SlcMessageSendJobVo jobVo = new SlcMessageSendJobVo();
        jobVo.setJobStartTm(System.currentTimeMillis());
        jobVo.setSrvMsgComSlcSendIvo(ivo);


        ScheduledExecutorService executorService = this.createScheduledExecutorService(sendTopicList.size());

        ArrayList<String> runningThreadList = new ArrayList<>();
        String testCode = String.format(testCdFormat, body.getTestCd(), ApCommonUtil.generateUUID(5));

        /**
         * Topic 개수만큼 Thread 발송 Thread 생성
         */
        for(String topicName: sendTopicList){

            String threadName = String.format(threadNameFormat, Thread.currentThread().getName(), topicName);
            log.info("Job Start {}", threadName);

            MsgSendTaskVo taskVo = MsgSendTaskVo.builder()
                    .topicName(topicName)
                    .testCd(testCode)
                    .targetSys(body.getSystemNm())
                    .service(executorService)
                    .runningThreadList(runningThreadList)
                    .myThreadName(threadName)
                    .bizExecuteCnt(body.getBizExecuteCnt())
                    .retentionSecond(body.getRetentionSecond())
                    .targetTps(body.getTargetTps())
                    .httpRequestVo(body.getHttpRequestVo() == null ? null : body.getHttpRequestVo())
                    .fisMsgSendReqVo(body.getFisMsgSendReqVo() == null ? null : body.getFisMsgSendReqVo())
                    .build();

            Runnable task = this.taskEveryOneSecond(taskVo);

            executorService.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
            // scheduleWithFixedDelay 작업 지연발생하여도 Async 하게 진행
        }

    }


    /**
     * 생성하기
     * @param senderCnt
     * @return
     */
    private ScheduledExecutorService createScheduledExecutorService(int senderCnt){
        int threadPoolCount = (int) Math.ceil(senderCnt * 1.5);

        return Executors.newScheduledThreadPool(threadPoolCount);

    }



    /**
     * 메시지 발송하는 실제 Task 메소드
     * 1초 마다 진행
     * @param vo
     * @return
     */
    private Runnable taskEveryOneSecond(MsgSendTaskVo vo){

        AtomicInteger invokeTime = new AtomicInteger(1);
        Long sleepMs = this.calculateDelayTimeWithTPS(vo.getTargetTps());
        log.info("Thread invoke for doing service. testCd: {}, threadName: {}, sleepMs: {}",
                vo.getTestCd(), vo.getMyThreadName(), sleepMs);

        return () -> {

            invokeTime.getAndIncrement();

            if(vo.getRetentionSecond() + 1 <= invokeTime.get()){


                vo.getRunningThreadList().remove(vo.getMyThreadName());
                log.info("Complete run.! invoke: {}, retention: {}", invokeTime, vo.getRetentionSecond());

                if(vo.getRunningThreadList().isEmpty()){
                    log.info("This is last thread. shut down service.");
                    vo.getService().shutdownNow();
                }
            }


            int unitMsgCnt = 0;

            while (true){
                ++unitMsgCnt;
                if(unitMsgCnt == vo.getTargetTps() + 1){
                    log.debug("Complete send thread");
                    break;
                } // TODO 작업 종료 시점

                String cid = String.format(cidNameFormat, vo.getTargetSys());
                String tid = String.format(tidFormat, vo.getTestCd(), vo.getMyThreadName(), invokeTime.get(),String.valueOf(unitMsgCnt));


                ApSysTestIvo.Body body = this.generateTestBody(vo.getTestCd(), vo.getTopicName(), sdf.format(new Date()),
                                                    vo.getBizExecuteCnt(), invokeTime.get(), unitMsgCnt);



                try {

                    String payload = this.generateTestPayload(tid, vo.getTargetSys(), cid, body);
                    HttpRequestVo requestVo = vo.getHttpRequestVo();

                    if(requestVo == null){



                        if(vo.getFisMsgSendReqVo() != null) {
                            FisMsgSendReqVo fisMsgSendReqVo = vo.getFisMsgSendReqVo();

                            int tps = vo.getTargetTps() < 20 ? 20 : vo.getTargetTps();
                            String fileFormat = "inspection_result_file_row_count_%s_%sTPS_241025.csv";
                            String fileName = String.format(fileFormat, String.valueOf(fisMsgSendReqVo.getFileRawCount()), String.valueOf(tps));

                            payload = this.generateFisPayload(tid, vo.getTargetSys(), fileName, vo.getTestCd());

                        }



                        if(vo.getRetentionSecond() + 1 <= invokeTime.get()){
                            continue;
                        }
                            InterfaceSolacePub.getInstance().sendTopicMessage(cid, payload, vo.getTopicName());

                    }else {


                        if(vo.getRetentionSecond() + 1 <= invokeTime.get()){
                            continue;
                        }
                        requestVo.setPayload(payload);
                        this.httpRequestService.sendHttpSyncRequest(requestVo);
                    }

                    String sendType = "BASIC";
                    if(vo.getFisMsgSendReqVo() != null) {sendType = "FIS";}
                    if(requestVo != null) {sendType = "HTTP";}

                    log.info("message send trace. Type:{}, Tid: {}, TargetSystem: {}, TopicName: {}, TestCd: {}, Run Thread. name: {}, invokeTime: {} unitMsgCnt: {}, paylaod: {}"
                            ,sendType, tid,vo.getTargetSys(), vo.getTopicName(), vo.getTestCd(), vo.getMyThreadName(), invokeTime, unitMsgCnt, payload);



                } catch (JCSMPException e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                try {
                    Thread.sleep(sleepMs);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        };
    }



    private static ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);


    /**
     *
     * @param testCd
     * @param topicNm
     * @param msgSendDt
     * @param bizExecuteCnt
     * @param loopMsgCnt
     * @param unitMsgCnt
     * @return
     */
    private ApSysTestIvo.Body generateTestBody(String testCd, String topicNm, String msgSendDt,
                                               int bizExecuteCnt, int loopMsgCnt, int unitMsgCnt){

        ApSysTestIvo.Body body = new ApSysTestIvo.Body();
        body.setTestCd(testCd);
        body.setRecvTopicNm(topicNm);
        body.setMsgSendDt(msgSendDt);
        body.setBizExecuteCnt(bizExecuteCnt);
        body.setLoopMsgCnt(loopMsgCnt);
        body.setUnitMsgCnt(unitMsgCnt);
        return  body;


    }

    /**
     * 테스트 메시지 전문 생성
     * @param targetSystem
     * @param cid
     * @param body
     * @return
     */
    private String generateTestPayload(String tid, String targetSystem, String cid, ApSysTestIvo.Body body) throws JsonProcessingException {

        ApSysTestIvo ivo = new ApSysTestIvo();
        ivo.setBody(body);

        AbsMsgHead head = AbsMsgHead.builder()
                .src("부하발생기")
                .tgt(targetSystem)
                .cid(cid)
                .tid(tid)
                .osrc("")
                .srcEqp("")
                .tgtEqp(new ArrayList<>())
                .build();

        ivo.setHead(head);

        return objectMapper.writeValueAsString(ivo);

    }


    /**
     * FIS 테스트 전문 생성
     * @param targetSystem
     * @param fileName
     * @return
     */
    private String generateFisPayload(String tid, String targetSystem, String fileName, String testCd) throws JsonProcessingException {

        FisFileReportVo ivo = new FisFileReportVo();

        FisFileReportVo.Body body = new FisFileReportVo.Body();
        body.setFileType("INSP");
        body.setFileName(fileName);
        body.setFilePath("/home/abscapp/data/app/fis/sample/APTEST/");

        body.setEqpId("AP-BU-10-01");
        body.setLotId(testCd);
        body.setProdMtrlId("testMtrlId");
        body.setMtrlFace("Top");
        body.setSiteId("SVM");
        body.setUserId("WFS");

        AbsMsgHead head = AbsMsgHead.builder()
                .src("WFS")
                .tgt(targetSystem)
                .cid("FIS_FILE_REPORT")
                .tid(tid)
                .osrc("")
                .srcEqp("")
                .tgtEqp(new ArrayList<>())
                .build();

        ivo.setHead(head);
        ivo.setBody(body);

        return objectMapper.writeValueAsString(ivo);

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
