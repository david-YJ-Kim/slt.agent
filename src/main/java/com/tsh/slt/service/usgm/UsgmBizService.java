package com.tsh.slt.service.usgm;

import com.tsh.slt.dao.entity.usgm.jpa.SnUsgmRdsEntity;
import com.tsh.slt.spec.usgm.*;

import java.util.List;

public interface UsgmBizService {

    // TODO Entity 바로 리턴하는 이슈 해결 필요


    /**
     * 저장된 모든 Record 리턴
     * @param ivo
     * @return
     */
    OiUsgmFetchRepIvo srvUsgmFetchReq(SrvUsgmFetchReqIvo ivo);
    
    
    /**
     * Git Info Vo 생성하여 저장
     * @param ivo
     * @return
     */
    SnUsgmRdsEntity srvUsgmNewRecord(SrvUsgmNewRecordIvo ivo);


    /**
     * 저장된 Git 정보를 수정
     * @param ivo
     * @return
     */
    SnUsgmRdsEntity srvUsgmEditRecord(SrvUsgmEditRecordIvo ivo);


    /**
     * 저장된 Git 정보를 삭제
     * @param ivo
     * @return
     */
    int srvUsgmDeleteRecord(SrvUsgmDeleteRecordIvo ivo);

    /**
     * 요청된 Git 에 대해서 Pull 수행
     * @param ivo
     * @return
     */
    int srvUsgmGitPull(SrvUsgmGitPullIvo ivo);


    /**
     * 요청된 Git 에 대해서 Push 수행
     * @param ivo
     * @return
     */
    int srvUsgmGitPush(SrvUsgmGitPushIvo ivo);
}

