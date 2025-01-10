package com.tsh.slt.spec.usgm;

import com.tsh.slt.interfaces.util.ApMessageList;
import com.tsh.slt.spec.common.AbsMsgCommonVo;
import com.tsh.slt.spec.common.ApMsgBody;
import lombok.Data;

@Data
public class SrvUsgmDeleteRecordIvo extends AbsMsgCommonVo {

    public static String cid = ApMessageList.SRV_USGM_DELETE_RECORD;

    Body body;

    @Data
    public static class Body extends ApMsgBody {

        /* 로컬에 저장된 소스 폴더 이름 */
        String repoFileName;
        /* 로컬 소스 폴더 경로 */
        String repoFilePath;
        /* 연결된 Git 원격 레포지토리 URL */
        String gitRepoUrl;
        /* 연결된 Git 원격 레포지토르 브랜치 이름 */
        String gitRepoBranchName;



    }

}
