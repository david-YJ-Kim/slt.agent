package com.tsh.slt.spec.usgm;

import com.tsh.slt.interfaces.util.ApMessageList;
import com.tsh.slt.spec.common.AbsMsgCommonVo;
import com.tsh.slt.spec.common.ApMsgBody;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class SrvUsgmFetchReqIvo extends AbsMsgCommonVo {

    public static String cid = ApMessageList.SRV_USGM_FETCH_REQ;

    Body body;

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    public static class Body extends ApMsgBody {

    }

}
