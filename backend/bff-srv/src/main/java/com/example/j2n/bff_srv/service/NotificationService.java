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
public class NotificationService {

    private final RestClientUtil restClientUtil;

    public Object getNotifications(int page, int size) {
        log.info("[BFF-SRV] Calling Notification-Srv to get notifications, page: {}, size: {}", page, size);
        String path = GatewayPath.NOTIFICATION_BASE_PATH + "?page=" + page + "&size=" + size;
        return restClientUtil.request(
                path,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Object>() {
                }
        );
    }

    public Object markAsRead(Long id) {
        log.info("[BFF-SRV] Calling Notification-Srv to mark notification {} as read", id);
        return restClientUtil.request(
                String.format(GatewayPath.NOTIFICATION_READ_PATH, id),
                HttpMethod.PUT,
                null,
                new ParameterizedTypeReference<Object>() {
                }
        );
    }

    public Object markAllAsRead() {
        log.info("[BFF-SRV] Calling Notification-Srv to mark all notifications as read");
        return restClientUtil.request(
                GatewayPath.NOTIFICATION_READ_ALL_PATH,
                HttpMethod.PUT,
                null,
                new ParameterizedTypeReference<Object>() {
                }
        );
    }
}
