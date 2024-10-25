package com.tsh.slt.service.solaceAction.multiMsgSend.vo;


import lombok.Data;

@Data
public class FisMsgSendReqVo {

    String fileType;
    String filePath;
    String fileName;
    int fileRawCount;
}
