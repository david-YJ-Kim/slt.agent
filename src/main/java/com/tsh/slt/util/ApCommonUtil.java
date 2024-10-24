package com.tsh.slt.util;

import java.util.UUID;

public class ApCommonUtil {

    public static String generateClientName(String groupName, String siteName, String envType, String processSeq){
        return String.format("%s-%s-%s-", groupName, siteName, envType) + String.format("%04d", Integer.valueOf(processSeq) );
    }


    /**
     * 요청한 길이만큼의 UUID 생성
     * @param maxLength
     * @return
     */
    public static String generateUUID(int maxLength){
        return UUID.randomUUID().toString().replace("-", "").substring(0, maxLength);
    }
}
