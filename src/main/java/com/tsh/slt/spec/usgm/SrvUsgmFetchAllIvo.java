package com.tsh.slt.spec.usgm;

import com.tsh.slt.interfaces.util.ApMessageList;
import com.tsh.slt.spec.common.AbsMsgCommonVo;
import com.tsh.slt.spec.common.ApMsgBody;
import lombok.Data;

@Data
public class SrvUsgmFetchAllIvo extends AbsMsgCommonVo {

    public static String cid = ApMessageList.SRV_USGM_FETCH_ALL;

    Body body;

    @Data
    public static class Body extends ApMsgBody {

    }

}
