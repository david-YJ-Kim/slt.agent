package com.tsh.slt.spec;


import com.tsh.slt.spec.common.AbsMsgCommonVo;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
public class FisFileReportVo extends AbsMsgCommonVo {


    Body body;

    @Getter
    @Setter
    @SuperBuilder
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
