package com.tsh.slt.service.httpRequest;


import com.tsh.slt.service.httpRequest.vo.HttpRequestVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
@Slf4j
public class HttpRequestService {


    // TODO 확인 필요, Autowired 로 해당 빈 생성 불가... 사유는.? Could not autowire. No beans of 'WebClient' type found.
    // → Spring 2.0 에서 자동으로 bean 생성하지 않기 때문에, 필요 시 @Configuration 으로 생성할 수 있음
    private final RestTemplate restTemplate = new RestTemplate();

    private final WebClient webClient = WebClient.create();


    /**
     * Sync 방식으로 Http 요청을 하는 메소드
     * RestTemplate.exchange() 메서드를 사용하여 HTTP 메서드를 동적으로 지정할 수 있습니다. 이 메서드는 다양한 HTTP 메서드(GET, POST, PUT, DELETE 등)를 지원하며, 요청 및 응답을 유연하게 처리합니다.
     *
     * @param vo
     * @return
     */
    public ResponseEntity<String> sendHttpSyncRequest(HttpRequestVo vo) {

        // Construct the URL from the server, port, and URI
        String url = vo.getTgtServer() + ":" + vo.getTgtPort() + vo.getUri();

        // Create HttpHeaders and add the headers from the requestVo
        HttpHeaders headers = new HttpHeaders();
        if (vo.getHeaders() != null) {
            headers.setAll(vo.getHeaders());
        }

        // Create HttpEntity using the payload and headers
        HttpEntity<String> entity = new HttpEntity<>(vo.getPayload(), headers);

        // Send the request based on the HTTP method specified in requestVo
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                vo.getMethod(),
                entity,
                String.class
        );

        return response;

    }


    /**
     * Async 방식으로 Http 요청을 하는 메소드
     * @param requestVo
     * @return
     */
    public Mono<HttpRequestVo> sendHttpAsyncRequest(HttpRequestVo requestVo) {

        // Construct the URL
//        String url = requestVo.getTgtServer() + ":" + requestVo.getTgtPort() + requestVo.getUri();


        return webClient.post().uri("/api/v1/resource").body(Mono.just(requestVo), HttpRequestVo.class).retrieve().bodyToMono(HttpRequestVo.class);

//        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(requestVo.getTgtServer() + ":" + requestVo.getTgtPort() + requestVo.getUri());
//        if (requestVo.getQueryParams() != null) {
//            requestVo.getQueryParams().forEach(uriBuilder::queryParam);
//        }
//        URI uri = uriBuilder.build().toUri();
//
//
//        // Create HttpHeaders and set the headers from the requestVo
//        HttpHeaders headers = new HttpHeaders();
//        if (requestVo.getHeaders() != null) {
//            headers.setAll(requestVo.getHeaders());
//        }
//
//        // Create the WebClient request
//        WebClient.RequestBodySpec requestSpec = webClient.method(requestVo.getMethod())
//                .uri(uri)
//                .headers(httpHeaders -> httpHeaders.putAll(headers));
//
//        // Add payload if method is POST or PUT
//        if (requestVo.getMethod() == HttpMethod.POST || requestVo.getMethod() == HttpMethod.PUT) {
//            return requestSpec.body(BodyInserters.fromValue(requestVo.getPayload()))
//                    .retrieve()
//                    .bodyToMono(String.class);
//        } else {
//            // No body for GET and DELETE
//            return requestSpec.retrieve()
//                    .bodyToMono(String.class);
//        }

    }

}
