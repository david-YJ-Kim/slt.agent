package com.tsh.slt.spec.common;

import lombok.Builder;
import lombok.Data;

@Data
public class ApMsgHead {

    String cid;
    String tid;

    @Builder
    public ApMsgHead(String cid, String tid) {
        this.cid = cid;
        this.tid = tid;
    }
}
