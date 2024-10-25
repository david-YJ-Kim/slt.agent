package com.tsh.slt.service.httpRequest.vo;


import com.tsh.slt.spec.SrvMsgComHttpSendIvo;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpMethod;

import java.util.Map;

@Data
@Builder
public class HttpRequestVo {

    private String tgtServer;          // 대상 서버 주소 (e.g., https://example.com)
    private String tgtPort;            // 대상 서버 포트 (e.g., 80, 443)
    private String uri;                // 요청할 URI (e.g., /api/v1/resource)
    private HttpMethod method;         // HTTP 요청 메소드 (GET, POST, PUT, DELETE 등)
    private String payload;            // 요청의 본문 (POST, PUT 등의 요청에 사용)
    private Map<String, String> headers; // HTTP 헤더 (Authorization, Content-Type 등)
    private Map<String, String> queryParams; // 쿼리 파라미터 (e.g., ?key=value 형식)

}


/**
 * 샘플
 * HttpRequestVo requestVo = new HttpRequestVo();
 * requestVo.setTgtServer("https://example.com");
 * requestVo.setTgtPort("443");
 * requestVo.setUri("/api/v1/resource");
 * requestVo.setMethod(HttpMethod.POST);
 * requestVo.setPayload("{\"name\": \"John\", \"age\": 30}");
 * requestVo.setHeaders(Map.of("Authorization", "Bearer token", "Content-Type", "application/json"));
 * requestVo.setQueryParams(Map.of("page", "1", "size", "10"));
 */