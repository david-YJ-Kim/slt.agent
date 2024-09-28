package com.tsh.slt.service.solaceAction.multiMsgSend;

import com.tsh.slt.config.ApPropertyObject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;


@Slf4j
public class SolaceActionUtil {


    /**
     * 요청 들어온 기준으로 발생해야하는 토픽 리스트 획득
     * @param systemName
     * @param sendTopicInfo: EQP(1-10),CMN(1-10)
     * @return
     */
    public static ArrayList<String> getSendTopicList(String systemName, String sendTopicInfo){


        ArrayList<String> topicList = new ArrayList<>();

        String siteId = ApPropertyObject.getInstance().getSiteName();
        String env = ApPropertyObject.getInstance().getEnvType();

        String topicFormat = "%s/%s/%s/%s/%s";
        //        "SVM/PROD/WFS/CMN/00"

        for(String info: sendTopicInfo.split(",")){

            // "("를 기준으로 split
            String[] parts = info.split("\\(");
            String queueType = parts[0]; // EQP

            // "("와 ")" 사이의 숫자 파싱
            String rangePart = parts[1].replace(")", ""); // "0-10"
            String[] range = rangePart.split("-");

            int start = Integer.parseInt(range[0]); // 시작 숫자
            int end = Integer.parseInt(range[1]);   // 끝 숫자

            // "00"부터 "10"까지 생성하여 리스트에 저장
            for (int i = start; i <= end; i++) {
                String formattedNumber = String.format("%02d", i); // 2자리로 포맷
                topicList.add(String.format(topicFormat, siteId, env, systemName, queueType, formattedNumber));
            }

        }

        log.info("Print parsing result. info: {}, result: {}", sendTopicInfo, topicList);

        return topicList;

    }


}
