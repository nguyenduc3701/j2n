package com.example.j2n.notification_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.dto.PagingResponse;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.exception.BaseServiceException;
import com.example.j2n.notification_srv.constant.MessageEnum;
import com.example.j2n.notification_srv.constant.NotificationType;
import com.example.j2n.notification_srv.messaging.event.BillNotificationEvent;
import com.example.j2n.notification_srv.repository.NotificationRepository;
import com.example.j2n.notification_srv.repository.entity.NotificationEntity;
import com.example.j2n.notification_srv.service.response.NotificationResponse;
import com.example.j2n.notification_srv.service.response.SearchNotificationsResponse;
import com.example.j2n.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @LogAround(message = "Get paginated notifications for user")
    public BaseResponse<SearchNotificationsResponse> getNotifications(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationEntity> notificationPage = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        List<NotificationResponse> responses = notificationPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        long unreadCount = notificationRepository.countByUserIdAndIsRead(userId, false);

        PagingResponse pagingResponse = new PagingResponse(
                (int) notificationPage.getTotalElements(),
                page,
                size
        );

        SearchNotificationsResponse data = SearchNotificationsResponse.builder()
                .notifications(responses)
                .page(pagingResponse)
                .unreadCount(unreadCount)
                .build();

        return ResponseFactory.success(BaseMessageEnum.SUCCESS, data);
    }

    @Transactional
    @LogAround(message = "Mark a notification as read")
    public BaseResponse<Object> markAsRead(Long notificationId, String userId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BaseServiceException(MessageEnum.NOTIFICATION_NOT_FOUND.withArgs(notificationId)));

        if (!notification.getUserId().equals(userId)) {
            throw new BaseServiceException(BaseMessageEnum.ACCESS_DENIED);
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);

        return ResponseFactory.success(MessageEnum.MARK_AS_READ_SUCCESS, null);
    }

    @Transactional
    @LogAround(message = "Mark all notifications as read for user")
    public BaseResponse<Object> markAllAsRead(String userId) {
        notificationRepository.markAllAsReadByUserId(userId);
        return ResponseFactory.success(MessageEnum.MARK_ALL_AS_READ_SUCCESS, null);
    }

    @LogAround(message = "Create in-app bill notification")
    public void createBillNotification(String userId, BillNotificationEvent event) {
        NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

        String title = String.format("Room Bill %s - Month %d", event.getRoomNumber(), event.getBillingMonth());
        String message = String.format("Total amount: %s VND. Please pay on time.",
                currencyFormat.format(event.getTotalAmount()));

        NotificationEntity notification = NotificationEntity.builder()
                .userId(userId)
                .type(NotificationType.BILL_CREATED)
                .title(title)
                .message(message)
                .build();

        notificationRepository.save(notification);
        log.info("[NOTIFICATION-SERVICE] In-app notification saved for user: {}, bill: {}", userId, event.getBillId());
    }

    private NotificationResponse toResponse(NotificationEntity entity) {
        return NotificationResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .type(entity.getType().name())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .isRead(entity.getIsRead())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
