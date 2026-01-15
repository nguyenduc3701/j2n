package com.example.j2n.bff_srv.utils;

import com.example.j2n.bff_srv.config.GatewayConfig;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.example.j2n.bff_srv.constant.MessageEnum;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
public class RestClientUtil {
    private final RestTemplate restTemplate;
    private final GatewayConfig gatewayConfig;

    public <T, R> T request(String path, HttpMethod method, R body, Class<T> responseType) {
        String url = gatewayConfig.getBaseUrl() + path;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("FROM-BFF", "true");

        // Lấy token từ request hiện tại nếu có
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes sra) {
            HttpServletRequest currentRequest = sra.getRequest();
            String token = (String) currentRequest.getAttribute("TOKEN");
            if (token != null && !token.isEmpty()) {
                headers.set("Authorization", "Bearer " + token);
            }
        }

        HttpEntity<R> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<T> response = restTemplate.exchange(url, method, entity, responseType);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException(
                        String.format(MessageEnum.GATEWAY_REQUEST_FAILED.getMessage(), url, response.getStatusCode()));
            } else {
                response.getBody();
            }
            return response.getBody();
        } catch (HttpStatusCodeException ex) {
            throw new RuntimeException(
                    String.format(MessageEnum.GATEWAY_REQUEST_FAILED.getMessage(), url, ex.getStatusCode()), ex);
        }
    }

    public <T> T requestUpload(
            String path,
            MultiValueMap<String, Object> body,
            Class<T> responseType) {
        // Validate bắt buộc phải có key "files"
        if (body == null || !body.containsKey("files")) {
            throw new RuntimeException(MessageEnum.FILE_NOT_FOUND.getMessage());
        }

        String url = gatewayConfig.getBaseUrl() + path;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("FROM-BFF", "true");

        // Forward token
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes sra) {
            HttpServletRequest currentRequest = sra.getRequest();
            String token = (String) currentRequest.getAttribute("TOKEN");
            if (token != null && !token.isEmpty()) {
                headers.set("Authorization", "Bearer " + token);
            }
        }

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<T> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    responseType);
            return response.getBody();
        } catch (HttpStatusCodeException ex) {
            throw new RuntimeException(
                    String.format(
                            MessageEnum.GATEWAY_REQUEST_FAILED.getMessage(),
                            url,
                            ex.getStatusCode()),
                    ex);
        }
    }
    public ResponseEntity<byte[]> requestBinary(String path) {
        String url = gatewayConfig.getBaseUrl() + path;
        HttpHeaders headers = new HttpHeaders();
        headers.set("FROM-BFF", "true");
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes sra) {
            HttpServletRequest currentRequest = sra.getRequest();
            String token = (String) currentRequest.getAttribute("TOKEN");
            if (token != null && !token.isEmpty()) {
                headers.set("Authorization", "Bearer " + token);
            }
        }
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                byte[].class
        );
    }

}
