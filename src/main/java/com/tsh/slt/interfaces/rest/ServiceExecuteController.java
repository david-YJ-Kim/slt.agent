package com.tsh.slt.interfaces.rest;


import com.tsh.slt.interfaces.util.ApMessageList;
import com.tsh.slt.service.solaceAction.multiMsgSend.SolaceMessageSend;
import com.tsh.slt.spec.SrvMsgComSlcSendIvo;
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
    SolaceMessageSend solaceMessageSend;

    @PostMapping(ApMessageList.SRV_MSG_COM_SLC_SEND)
    public ResponseEntity executeRequest(@RequestBody SrvMsgComSlcSendIvo ivo) {


        log.info(ivo.toString());
        this.solaceMessageSend.executeJob(ivo);
//        log.info(jobVo.toString());

        return new ResponseEntity(HttpStatus.OK);
    }
}
