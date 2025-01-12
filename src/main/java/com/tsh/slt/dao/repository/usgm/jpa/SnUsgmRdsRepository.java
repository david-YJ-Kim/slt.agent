package com.tsh.slt.dao.repository.usgm.jpa;

import com.tsh.slt.dao.entity.usgm.jpa.SnUsgmRdsEntity;
import com.tsh.slt.util.code.UseStatCd;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SnUsgmRdsRepository extends JpaRepository<SnUsgmRdsEntity, String> {


    /**
     * Usable 상태인 항목만 전체 조회
     * @param useStatCd
     * @return
     */
    List<SnUsgmRdsEntity> findByUseStatCd(UseStatCd useStatCd);

    SnUsgmRdsEntity findByLclRpNmAndLclRpPthAndRmtRpUrlAndRmtRpBrnNmAndUseStatCd(String lclRpNm, String lclRpPath, String rmtRpUrl, String rmtRpBrnNm, UseStatCd useStatCd);

}
