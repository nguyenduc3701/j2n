package com.example.j2n.notification_srv.service;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.exception.AccessDeniedException;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.notification_srv.constant.MessageEnum;
import com.example.j2n.notification_srv.constant.NotificationType;
import com.example.j2n.notification_srv.messaging.event.BillNotificationEvent;
import com.example.j2n.notification_srv.repository.NotificationRepository;
import com.example.j2n.notification_srv.repository.entity.NotificationEntity;
import com.example.j2n.notification_srv.service.response.SearchNotificationsResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void getNotifications_Success() {
        // Arrange
        String userId = "user-123";
        Pageable pageable = PageRequest.of(0, 10);
        
        NotificationEntity entity = NotificationEntity.builder()
                .id(1L)
                .userId(userId)
                .type(NotificationType.BILL_CREATED)
                .title("Test Title")
                .message("Test Message")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        Page<NotificationEntity> page = new PageImpl<>(List.of(entity), pageable, 1);

        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(eq(userId), any(Pageable.class))).thenReturn(page);
        when(notificationRepository.countByUserIdAndIsRead(userId, false)).thenReturn(1L);

        // Act
        BaseResponse<SearchNotificationsResponse> response = notificationService.getNotifications(userId, 0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(String.valueOf(BaseMessageEnum.SUCCESS.getHttpStatus().getCode()), response.getCode());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().getNotifications().size());
        assertEquals(1L, response.getData().getNotifications().get(0).getId());
        assertEquals("BILL_CREATED", response.getData().getNotifications().get(0).getType());
        assertEquals(1L, response.getData().getUnreadCount());
        assertEquals(1, response.getData().getPage().getTotal());

        verify(notificationRepository, times(1)).findByUserIdOrderByCreatedAtDesc(eq(userId), any(Pageable.class));
        verify(notificationRepository, times(1)).countByUserIdAndIsRead(userId, false);
    }

    @Test
    void markAsRead_Success() {
        // Arrange
        Long notificationId = 1L;
        String userId = "user-123";
        NotificationEntity notification = NotificationEntity.builder()
                .id(notificationId)
                .userId(userId)
                .isRead(false)
                .build();

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(NotificationEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        BaseResponse<Object> response = notificationService.markAsRead(notificationId, userId);

        // Assert
        assertNotNull(response);
        assertEquals(String.valueOf(MessageEnum.MARK_AS_READ_SUCCESS.getHttpStatus().getCode()), response.getCode());
        assertTrue(notification.getIsRead());
        verify(notificationRepository, times(1)).findById(notificationId);
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    void markAsRead_NotFound_ThrowsDataNotFoundException() {
        // Arrange
        Long notificationId = 1L;
        String userId = "user-123";
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        // Act & Assert
        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> {
            notificationService.markAsRead(notificationId, userId);
        });

        assertTrue(exception.getMessage().contains("Notification not found with ID: " + notificationId));
        verify(notificationRepository, times(1)).findById(notificationId);
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void markAsRead_AccessDenied_ThrowsAccessDeniedException() {
        // Arrange
        Long notificationId = 1L;
        String userId = "user-123";
        NotificationEntity notification = NotificationEntity.builder()
                .id(notificationId)
                .userId("other-user")
                .isRead(false)
                .build();

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        // Act & Assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            notificationService.markAsRead(notificationId, userId);
        });

        assertEquals(BaseMessageEnum.ACCESS_DENIED.getMessage(), exception.getMessage());
        verify(notificationRepository, times(1)).findById(notificationId);
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void markAllAsRead_Success() {
        // Arrange
        String userId = "user-123";
        when(notificationRepository.markAllAsReadByUserId(userId)).thenReturn(5);

        // Act
        BaseResponse<Object> response = notificationService.markAllAsRead(userId);

        // Assert
        assertNotNull(response);
        assertEquals(String.valueOf(MessageEnum.MARK_ALL_AS_READ_SUCCESS.getHttpStatus().getCode()), response.getCode());
        verify(notificationRepository, times(1)).markAllAsReadByUserId(userId);
    }

    @Test
    void createBillNotification_Success() {
        // Arrange
        String userId = "user-123";
        BillNotificationEvent event = BillNotificationEvent.builder()
                .billId("bill-999")
                .roomNumber("102")
                .billingMonth(5)
                .totalAmount(BigDecimal.valueOf(1500000))
                .build();

        ArgumentCaptor<NotificationEntity> captor = ArgumentCaptor.forClass(NotificationEntity.class);

        // Act
        notificationService.createBillNotification(userId, event);

        // Assert
        verify(notificationRepository, times(1)).save(captor.capture());
        NotificationEntity savedEntity = captor.getValue();
        assertNotNull(savedEntity);
        assertEquals(userId, savedEntity.getUserId());
        assertEquals(NotificationType.BILL_CREATED, savedEntity.getType());
        assertEquals("Room Bill 102 - Month 5", savedEntity.getTitle());
        assertTrue(savedEntity.getMessage().contains("1.500.000 VND"));
    }
}
