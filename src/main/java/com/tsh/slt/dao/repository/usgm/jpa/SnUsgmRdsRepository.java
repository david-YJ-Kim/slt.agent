package com.tsh.slt.dao.repository.usgm.jpa;

import com.tsh.slt.dao.entity.usgm.jpa.SnUsgmRdsEntity;
import com.tsh.slt.util.code.UseStatCd;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SnUsgmRdsRepository extends JpaRepository<SnUsgmRdsEntity, String> {


    /**
     * Usable 상태인 항목만 조회하기
     * @param useStatCd
     * @return
     */
    List<SnUsgmRdsEntity> findByUseStatCd(UseStatCd useStatCd);

}
