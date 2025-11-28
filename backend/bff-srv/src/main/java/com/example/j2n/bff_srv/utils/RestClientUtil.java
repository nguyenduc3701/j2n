package com.example.j2n.bff_srv.utils;

import com.example.j2n.bff_srv.config.GatewayConfig;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.example.j2n.bff_srv.constant.MessageEnum;

@Component
@RequiredArgsConstructor
public class RestClientUtil {
    private final RestTemplate restTemplate;
    private final GatewayConfig gatewayConfig;

    public <T, R> T request(String path, HttpMethod method, R body, Class<T> responseType) {
        String url = gatewayConfig.getBaseUrl() + path;

        HttpHeaders headers = new HttpHeaders();    
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<R> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<T> response = restTemplate.exchange(url, method, entity, responseType);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException(String.format(MessageEnum.GATEWAY_REQUEST_FAILED.getMessage(), url, response.getStatusCode()));
            } else {
                response.getBody();
            }
            return response.getBody();
        } catch (HttpStatusCodeException ex) {
            throw new RuntimeException(String.format(MessageEnum.GATEWAY_REQUEST_FAILED.getMessage(), url, ex.getStatusCode()), ex);
        }
    }
}
