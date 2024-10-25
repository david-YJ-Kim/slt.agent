package com.tsh.slt.interfaces.rest;


import com.tsh.slt.interfaces.util.ApMessageList;
import com.tsh.slt.service.httpRequest.HttpRequestService;
import com.tsh.slt.service.httpRequest.vo.HttpRequestVo;
import com.tsh.slt.service.solaceAction.multiMsgSend.MessageSendService;
import com.tsh.slt.spec.SrvMsgComHttpSendIvo;
import com.tsh.slt.spec.SrvMsgComSlcSendIvo;
import com.tsh.slt.spec.common.AbsMsgHead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/srv")
@RequiredArgsConstructor
@Slf4j
public class ServiceExecuteController {


    @Autowired
    MessageSendService messageSendService;


    @Autowired
    HttpRequestService httpRequestService;


    /**
     * 테스트 메시지 발송하는 엔드포인트
     * 케이스 1. 일반 부하 발생기
     * 케이스 2. FIS 부하 발생기
     * 케이스 3. HTTP 부하 발생기
     *
     * @param ivo
     * @return
     */
    @PostMapping(ApMessageList.SRV_MSG_COM_SLC_SEND)
    public ResponseEntity executeRequest(@RequestBody SrvMsgComSlcSendIvo ivo) {

        AbsMsgHead head = ivo.getHead();
        String cid = head.getCid();

        log.info("Message send {}", cid);
        this.messageSendService.executeJob(ivo);

        return new ResponseEntity(HttpStatus.OK);
    }


    /**
     * 단일 HTTP 메시지 발송하는 엔드포인트
     * 
     * @param ivo
     * @return
     */
    @PostMapping(ApMessageList.SRV_MSG_COM_HTTP_SEND)
    public ResponseEntity executeRequest(@RequestBody SrvMsgComHttpSendIvo ivo) {

        log.info(ivo.toString());

            HttpRequestVo requestVo = ivo.getBody().getHttpRequestVo();

            this.httpRequestService.sendHttpSyncRequest(requestVo);
            return new ResponseEntity(HttpStatus.OK);
        }

}
