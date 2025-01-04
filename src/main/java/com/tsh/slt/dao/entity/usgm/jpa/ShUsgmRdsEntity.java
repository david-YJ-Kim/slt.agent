package com.tsh.slt.dao.entity.usgm.jpa;

import com.tsh.slt.util.code.UseStatCd;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "SH_USGM_RDS")
@DynamicInsert
@DynamicUpdate
@Cacheable(false)
public class ShUsgmRdsEntity {

    @Id
    @GenericGenerator(name = "SH_USGM_RDS_SEQ_GENERATOR", strategy = "com.tsh.slt.util.ObjIdGenerator")
    @GeneratedValue(generator = "SH_USGM_RDS_SEQ_GENERATOR")
    private String objId;

    private String refObjId;
    private String lclRpNm;
    private String lclRpPth;
    private String rmtRpUrl;
    private String rmtRpBrnNm;
    private String email;
    private String pwdHsh;
    private String salt;

    /** 공통 컬럼 **/

    private String evntNm;
    private String prevEvntNm;
    private String cstmEvntNm;
    private String prevCstmEvntNm;

    @Enumerated(EnumType.STRING)
    @Column(name="USE_STAT_CD")
    private UseStatCd useStatCd;

    private String rsnCd;
    private String trnsCm;
    private String crtUserId;
    private String crtDt;
    private String mdfyUserId;
    private String mdfyDt;
    private String fnlEvntDt;
    private String tid;


    @Builder
    public ShUsgmRdsEntity(String refObjId, String lcl_rp_nm, String lcl_rp_pth, String rmt_rp_url, String rmt_rp_brn_nm, String email, String pwd_hsh, String salt, String evnt_nm, String prev_evnt_nm, String cstm_evnt_nm, String prev_cstm_evnt_nm, UseStatCd use_stat_cd, String rsn_cd, String trns_cm, String crt_user_id, String crt_dt, String mdfy_user_id, String mdfy_dt, String fnl_evnt_dt, String tid) {
        this.refObjId = refObjId;
        this.lclRpNm = lcl_rp_nm;
        this.lclRpPth = lcl_rp_pth;
        this.rmtRpUrl = rmt_rp_url;
        this.rmtRpBrnNm = rmt_rp_brn_nm;
        this.email = email;
        this.pwdHsh = pwd_hsh;
        this.salt = salt;
        this.evntNm = evnt_nm;
        this.prevEvntNm = prev_evnt_nm;
        this.cstmEvntNm = cstm_evnt_nm;
        this.prevCstmEvntNm = prev_cstm_evnt_nm;
        this.useStatCd = use_stat_cd;
        this.rsnCd = rsn_cd;
        this.trnsCm = trns_cm;
        this.crtUserId = crt_user_id;
        this.crtDt = crt_dt;
        this.mdfyUserId = mdfy_user_id;
        this.mdfyDt = mdfy_dt;
        this.fnlEvntDt = fnl_evnt_dt;
        this.tid = tid;
    }

    public ShUsgmRdsEntity (SnUsgmRdsEntity crntEntity){

        this.refObjId = crntEntity.getObjId();
        this.lclRpNm = crntEntity.getLclRpNm();
        this.lclRpPth = crntEntity.getLclRpPth();
        this.rmtRpUrl = crntEntity.getRmtRpUrl();
        this.rmtRpBrnNm = crntEntity.getRmtRpBrnNm();
        this.email = crntEntity.getEmail();
        this.pwdHsh = crntEntity.getPwdHsh();
        this.salt = crntEntity.getSalt();
        this.evntNm = crntEntity.getEvntNm();
        this.prevEvntNm = crntEntity.getPrevEvntNm();
        this.cstmEvntNm = crntEntity.getCstmEvntNm();
        this.prevCstmEvntNm = crntEntity.getPrevCstmEvntNm();
        this.useStatCd = crntEntity.getUseStatCd();
        this.rsnCd = crntEntity.getRsnCd();
        this.trnsCm = crntEntity.getTrnsCm();
        this.crtUserId = crntEntity.getCrtUserId();
        this.crtDt = crntEntity.getCrtDt();
        this.mdfyUserId = crntEntity.getMdfyUserId();
        this.mdfyDt = crntEntity.getMdfyDt();
        this.fnlEvntDt = crntEntity.getFnlEvntDt();
        this.tid = crntEntity.getTid();


    }

    @Override
    public String toString() {
        return "ShUsgmRdsEntity{" +
                "objId='" + objId + '\'' +
                ", refObjId='" + refObjId + '\'' +
                ", lclRpNm='" + lclRpNm + '\'' +
                ", lclRpPth='" + lclRpPth + '\'' +
                ", rmtRpUrl='" + rmtRpUrl + '\'' +
                ", rmtRpBrnNm='" + rmtRpBrnNm + '\'' +
                ", email='" + email + '\'' +
                ", pwdHsh='" + pwdHsh + '\'' +
                ", salt='" + salt + '\'' +
                ", evntNm='" + evntNm + '\'' +
                ", prevEvntNm='" + prevEvntNm + '\'' +
                ", cstmEvntNm='" + cstmEvntNm + '\'' +
                ", prevCstmEvntNm='" + prevCstmEvntNm + '\'' +
                ", useStatCd=" + useStatCd +
                ", rsnCd='" + rsnCd + '\'' +
                ", trnsCm='" + trnsCm + '\'' +
                ", crtUserId='" + crtUserId + '\'' +
                ", crtDt=" + crtDt +
                ", mdfyUserId='" + mdfyUserId + '\'' +
                ", mdfyDt=" + mdfyDt +
                ", fnlEvntDt=" + fnlEvntDt +
                ", tid='" + tid + '\'' +
                '}';
    }
}
