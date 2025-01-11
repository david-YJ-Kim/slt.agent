package com.tsh.slt.interfaces.util;

public final class ApMessageList {

    public static final String AP_HEALTH_TEST = "AP_HEALTH_TEST";


    /**
     * Test 메시지 발송용
     */
    public static final String AP_SYS_TEST = "AP_SYS_TEST";
    public static final String AP_FIS_TEST = "AP_FIS_TEST";
    public static final String AP_HTTP_TEST = "AP_HTTP_TEST";


    /**
     * SRV
     */
    
    /*
    MSG_COM: messageComm 서비스 클래스
     */
    // SLC SEND : SolaceMessageSend
    public static final String SRV_MSG_COM_SLC_SEND = "SRV_MSG_COM_SLC_SEND";
    public static final String SRV_MSG_COM_HTTP_SEND = "SRV_MSG_COM_HTTP_SEND";


    /*
    USGM 서비스
     */
    // 신규 레코드 등록
    public static final String SRV_USGM_NEW_RECORD = "SRV_USGM_NEW_RECORD";
    // 전체 레코드 가져오기
    public static final String SRV_USGM_FETCH_REQ = "SRV_USGM_FETCH_REQ";
    // OI 수신, 조회 오청에 대한 응답
    public static final String OI_USGM_FETCH_REP = "OI_USGM_FETCH_REP";
    // 특정 레코드 수정
    public static final String SRV_USGM_EDIT_RECORD = "SRV_USGM_EDIT_RECORD";
    // 특정 레코드 삭제
    public static final String SRV_USGM_DELETE_RECORD = "SRV_USGM_DELETE_RECORD";
    // 특정 레코드 Git Pull
    public static final String SRV_USGM_GIT_PULL = "SRV_USGM_GIT_PULL";
    // 특정 레코드 Git Push
    public static final String SRV_USGM_GIT_PUSH = "SRV_USGM_GIT_PUSH";


}
