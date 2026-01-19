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
import com.example.j2n.bff_srv.dto.DownstreamMessage;
import com.example.j2n.bff_srv.service.response.BaseResponse;
import com.example.j2n.enums.HttpStatusCode;
import com.example.j2n.exception.ExternalServiceException;
import com.example.j2n.exception.InvalidInputException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
public class RestClientUtil {
    private final RestTemplate restTemplate;
    private final GatewayConfig gatewayConfig;
    private final ObjectMapper objectMapper;

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
                throw new ExternalServiceException(MessageEnum.GATEWAY_REQUEST_FAILED);
            } else {
                response.getBody();
            }
            return response.getBody();
        } catch (HttpStatusCodeException ex) {
            throw buildExternalException(ex);
        }
    }

    public <T> T requestUpload(
            String path,
            MultiValueMap<String, Object> body,
            Class<T> responseType) {
        // Validate bắt buộc phải có key "files"
        if (body == null || !body.containsKey("files")) {
            throw new InvalidInputException(MessageEnum.FILE_NOT_FOUND);
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
            throw buildExternalException(ex);
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
        try {
            return restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    byte[].class);
        } catch (HttpStatusCodeException ex) {
            throw buildExternalException(ex);
        }
    }

    private ExternalServiceException buildExternalException(HttpStatusCodeException ex) {
        try {
            BaseResponse<?> downstream = objectMapper.readValue(
                    ex.getResponseBodyAsString(),
                    BaseResponse.class);

            return new ExternalServiceException(
                    new DownstreamMessage(
                            downstream.getCode(),
                            HttpStatusCode.from(ex.getStatusCode().value()),
                            downstream.getMessage()),
                    ex);

        } catch (JsonProcessingException je) {
            return new ExternalServiceException(
                    MessageEnum.GATEWAY_REQUEST_FAILED,
                    je);
        }
    }

}
