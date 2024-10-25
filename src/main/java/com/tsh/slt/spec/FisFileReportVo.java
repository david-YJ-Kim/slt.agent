package com.tsh.slt.spec;


import com.tsh.slt.spec.common.AbsMsgCommonVo;
import lombok.Data;

import java.util.List;

@Data
public class FisFileReportVo extends AbsMsgCommonVo {


    Body body;

    @Data
    public static class Body {

        String siteId;
        String userId;
        String mdfyUserId;
        String rsnCd;
        String trnsCm;

        String eqpId;
        String lotId;
        String prodMtrlId;
        String mtrlFace;
        String fileType;
        String filePath;
        String fileName;

    }
}
