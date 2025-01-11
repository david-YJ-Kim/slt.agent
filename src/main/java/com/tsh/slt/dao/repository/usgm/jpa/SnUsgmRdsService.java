package com.tsh.slt.dao.repository.usgm.jpa;

import com.tsh.slt.dao.dto.usgm.SaveRecordDto;
import com.tsh.slt.dao.entity.usgm.jpa.ShUsgmRdsEntity;
import com.tsh.slt.dao.entity.usgm.jpa.SnUsgmRdsEntity;
import com.tsh.slt.spec.usgm.SrvUsgmNewRecordIvo;
import com.tsh.slt.util.code.UseStatCd;
import com.tsh.slt.util.service.vo.SecurityInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@Slf4j
public class SnUsgmRdsService {

    @Autowired
    SnUsgmRdsRepository snUsgmRdsRepository;

    @Autowired
    ShUsgmRdsRepository shUsgmRdsRepository;

    /**
     * Usable 상태인 항목만 조회하기
     * @return
     */
    public List<SnUsgmRdsEntity> findByUseStatCd(){

        return this.snUsgmRdsRepository.findByUseStatCd(UseStatCd.Usable);
    }



    /**
     * Save new entity at repository. optional save in history
     * @param ivo
     * @param updateHistoryYn
     * @return
     */
    public SnUsgmRdsEntity saveNewRecord(SrvUsgmNewRecordIvo ivo, SecurityInfoVo securityInfoVo, boolean updateHistoryYn){

        log.info(ivo.toString());

        SrvUsgmNewRecordIvo.Body body = ivo.getBody();

        SnUsgmRdsEntity newRecord = SnUsgmRdsEntity.builder()
                .lcl_rp_nm(body.getRepoFileName())
                .lcl_rp_pth(body.getRepoFilePath())
                .rmt_rp_url(body.getGitRepoUrl())
                .rmt_rp_brn_nm(body.getGitRepoBranchName())
                .email(body.getGitEmail())
                .pwd_hsh(securityInfoVo.getHashedPwd())
                .salt(securityInfoVo.getSalt())

                // TODO 공통 컬럼 처리하기
                .evnt_nm(Thread.currentThread().getStackTrace()[2].getMethodName())
                .use_stat_cd(UseStatCd.Usable)
                .crt_user_id(body.getUserId())
                .mdfy_user_id(body.getUserId())


                // TODO sqlite 는 timestamp 가 별도 없어 String으로 처리, Timestamp로 저장 시, Query 해올 때 문제가 발생.
                .crt_dt(String.valueOf(Timestamp.from(Instant.now())))
                .mdfy_dt(String.valueOf(Timestamp.from(Instant.now())))
                .fnl_evnt_dt(String.valueOf(Timestamp.from(Instant.now())))
                
                // TODO tid 관리 방안
                .tid(ivo.getHead().getTid())
                .build();

        log.info(newRecord.toString());

        SnUsgmRdsEntity savedRecord = this.snUsgmRdsRepository.save(newRecord);

        if(updateHistoryYn){
            this.shUsgmRdsRepository.save(new ShUsgmRdsEntity(savedRecord));
        }

        return savedRecord;


    }



}
