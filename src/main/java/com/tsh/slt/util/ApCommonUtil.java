package com.tsh.slt.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.UUID;

@Slf4j
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


    /**
     * Key 구성에 필요한 인자들과 구분자를 받아 concat 하여 key 생성
     * 호출 예제) ApCommonUtil.generateKey("|", new String[]{"A","B","C"})
     * @param delimiter: 구분자 ex) |, _ , etc...
     * @param parameters: 키 구성 인자 ex) A,B,C,D
     * @return
     */
    public static String generateKey(String delimiter, String... parameters){

        log.info("Generate key. delimiter: {}, parameters: {}", delimiter, Arrays.toString(parameters));

        StringBuilder key = new StringBuilder();
        for(int i = 0; i < parameters.length; i++){
            key.append(parameters[i]);

            if(i != parameters.length - 1){
                key.append(delimiter);
            }
        }

        return key.toString();

    }

}
