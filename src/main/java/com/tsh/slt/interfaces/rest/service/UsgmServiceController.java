package com.tsh.slt.interfaces.rest.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsh.slt.interfaces.util.ApMessageList;
import com.tsh.slt.service.usgm.UpstreamManageService;
import com.tsh.slt.spec.usgm.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/srv/usgm")
@RequiredArgsConstructor
@Slf4j
public class UsgmServiceController {


    @Autowired
    UpstreamManageService upstreamManageService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/{messageName}")
    public ResponseEntity<?> usgmServiceExecute ( @PathVariable String messageName,
                                                  @RequestBody Object requestBody){

        log.info("Request message: {}. it's payload: {}", messageName, requestBody.toString());
        try{
            switch (messageName){
                case ApMessageList.SRV_USGM_NEW_RECORD:
                    // TODO Reflection 을 통해 동적 호출 적용 대상
                    SrvUsgmNewRecordIvo srvUsgmNewRecordIvo = this.objectMapper.convertValue(requestBody, SrvUsgmNewRecordIvo.class);
                    return ResponseEntity.ok().body(this.upstreamManageService.srvUsgmNewRecord(srvUsgmNewRecordIvo));

                case ApMessageList.SRV_USGM_GIT_PULL:
                    if (requestBody instanceof SrvUsgmGitPullIvo) {
                        SrvUsgmGitPullIvo ivo = (SrvUsgmGitPullIvo) requestBody;
                    }
                    break;
                case ApMessageList.SRV_USGM_GIT_PUSH:
                    if (requestBody instanceof SrvUsgmGitPushIvo) {
                        SrvUsgmGitPushIvo ivo = (SrvUsgmGitPushIvo) requestBody;
                    }
                    break;
                case ApMessageList.SRV_USGM_DELETE_RECORD:
                    if (requestBody instanceof SrvUsgmDeleteRecordIvo) {
                        SrvUsgmDeleteRecordIvo ivo = (SrvUsgmDeleteRecordIvo) requestBody;
                    }
                    break;
                case ApMessageList.SRV_USGM_EDIT_RECORD:
                    if (requestBody instanceof SrvUsgmEditRecordIvo) {
                        SrvUsgmEditRecordIvo ivo = (SrvUsgmEditRecordIvo) requestBody;
                    }
                    break;
                case ApMessageList.SRV_USGM_FETCH_ALL:
                    if (requestBody instanceof SrvUsgmFetchAllIvo) {
                        SrvUsgmFetchAllIvo ivo = (SrvUsgmFetchAllIvo) requestBody;
                    }
                    break;
                default:
                    return ResponseEntity.badRequest().body("Unsupported message ID: " + messageName);
            }

        }catch (Exception e){
            log.error("Error processing request for messageName: " + messageName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request: " + e.getMessage());
        }
        return null;
    }
}
