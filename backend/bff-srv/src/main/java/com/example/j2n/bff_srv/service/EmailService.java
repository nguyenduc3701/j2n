package com.example.j2n.bff_srv.service;

import com.example.j2n.bff_srv.constant.GatewayPath;
import com.example.j2n.bff_srv.utils.RestClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final RestClientUtil restClientUtil;

    public Object sendEmail(Object request) {
        log.info("[BFF-SRV] Calling Notification-Srv to send email");
        return restClientUtil.request(
                GatewayPath.NOTIFICATION_EMAIL_SEND_PATH,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<Object>() {
                }
        );
    }
}
