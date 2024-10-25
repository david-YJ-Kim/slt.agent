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

    @PostMapping(ApMessageList.SRV_MSG_COM_SLC_SEND)
    public ResponseEntity executeRequest(@RequestBody SrvMsgComSlcSendIvo ivo) {

        AbsMsgHead head = ivo.getHead();
        String cid = head.getCid();

        log.info("Message send {}", cid);
        switch (cid) {
            case ApMessageList.AP_SYS_TEST:
                this.messageSendService.executeJob(ivo);
                break;

            case ApMessageList.AP_FIS_TEST:
                break;



        }

        return new ResponseEntity(HttpStatus.OK);
    }



        @PostMapping(ApMessageList.SRV_MSG_COM_HTTP_SEND)
        public ResponseEntity executeRequest(@RequestBody SrvMsgComHttpSendIvo ivo) {

        log.info(ivo.toString());

            HttpRequestVo requestVo = ivo.getBody().getHttpRequestVo();

            this.httpRequestService.sendHttpSyncRequest(requestVo);
            return new ResponseEntity(HttpStatus.OK);
        }

}
