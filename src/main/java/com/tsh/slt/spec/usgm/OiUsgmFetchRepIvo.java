package com.tsh.slt.spec.usgm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsh.slt.interfaces.util.ApMessageList;
import com.tsh.slt.spec.common.AbsMsgCommonVo;
import com.tsh.slt.spec.common.AbsMsgHead;
import com.tsh.slt.spec.common.ApMsgBody;
import com.tsh.slt.spec.usgm.common.GitUnitRecordVo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class OiUsgmFetchRepIvo extends AbsMsgCommonVo {



    public static String cid = ApMessageList.OI_USGM_FETCH_REP;

    Body body;

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    public static class Body extends ApMsgBody {
        List<GitUnitRecordVo> data;

    }



    public static void main(String[] args) throws JsonProcessingException {
        OiUsgmFetchRepIvo ivo = OiUsgmFetchRepIvo.builder()
                .head(
                        AbsMsgHead.builder()
                                .cid("cid")
                                .tid("tid")
                                .src("src")
                                .build()
                )
                .body(
                        Body.builder()
                                .userId("userId")
                                .siteId("siteId")
                                .data(
                                        new ArrayList<>(Arrays.asList(
                                                GitUnitRecordVo.builder()
                                                        .objId("objId")
                                                        .build(),
                                                GitUnitRecordVo.builder()
                                                        .objId("objIdA")
                                                        .build(),
                                                GitUnitRecordVo.builder()
                                                        .objId("objIdB")
                                                        .build()
                                        ))
                                )
                                .build()
                )
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(ivo));

    }
}


/*
{
   "head": {
      "cid": "cid",
      "tid": "tid",
      "osrc": null,
      "otgt": null,
      "src": "src",
      "srcEqp": null,
      "tgt": null,
      "tgtEqp": null
   },
   "body": {
      "siteId": "siteId",
      "userId": "userId",
      "data": [
         {
            "objId": "objId",
            "fileName": null,
            "localPath": null,
            "upstreamUrl": null,
            "branch": null,
            "gitEmail": null,
            "gitToken": null
         },
         {
            "objId": "objIdA",
            "fileName": null,
            "localPath": null,
            "upstreamUrl": null,
            "branch": null,
            "gitEmail": null,
            "gitToken": null
         },
         {
            "objId": "objIdB",
            "fileName": null,
            "localPath": null,
            "upstreamUrl": null,
            "branch": null,
            "gitEmail": null,
            "gitToken": null
         }
      ]
   }
}
 */