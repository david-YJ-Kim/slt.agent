package com.tsh.slt.spec.common;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ApMsgBody {

    private String siteId;
    private String userId;
    /* Request 요청에 대한 처리 결과 공유 */
    private MsgReasonVo reason;
}
