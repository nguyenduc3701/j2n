package com.example.j2n.notification_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.notification_srv.constant.MessageEnum;
import com.example.j2n.notification_srv.filter.InternalAuthFilter;
import com.example.j2n.notification_srv.service.NotificationService;
import com.example.j2n.notification_srv.service.response.SearchNotificationsResponse;
import com.example.j2n.security.InternalUserAuthentication;
import com.example.j2n.utils.ResponseFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private InternalAuthFilter internalAuthFilter;

    @MockitoBean
    private org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory;

    private static final String USER_ID = "user-123";

    @BeforeEach
    void setUp() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new InternalUserAuthentication(USER_ID, "testuser", "USER"));
        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getNotifications_Success() throws Exception {
        // Arrange
        SearchNotificationsResponse serviceResponse = SearchNotificationsResponse.builder()
                .notifications(Collections.emptyList())
                .unreadCount(0L)
                .build();
        BaseResponse<SearchNotificationsResponse> baseResponse = ResponseFactory.success(serviceResponse);

        when(notificationService.getNotifications(eq(USER_ID), anyInt(), anyInt())).thenReturn(baseResponse);

        // Act & Assert
        mockMvc.perform(get("/notifications")
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.unread_count").value(0));

        verify(notificationService, times(1)).getNotifications(USER_ID, 0, 20);
    }

    @Test
    void markAsRead_Success() throws Exception {
        // Arrange
        Long notificationId = 1L;
        BaseResponse<Object> baseResponse = ResponseFactory.of(MessageEnum.MARK_AS_READ_SUCCESS, null);

        when(notificationService.markAsRead(notificationId, USER_ID)).thenReturn(baseResponse);

        // Act & Assert
        mockMvc.perform(put("/notifications/" + notificationId + "/read")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("[200102] Notification marked as read"));

        verify(notificationService, times(1)).markAsRead(notificationId, USER_ID);
    }

    @Test
    void markAllAsRead_Success() throws Exception {
        // Arrange
        BaseResponse<Object> baseResponse = ResponseFactory.of(MessageEnum.MARK_ALL_AS_READ_SUCCESS, null);

        when(notificationService.markAllAsRead(USER_ID)).thenReturn(baseResponse);

        // Act & Assert
        mockMvc.perform(put("/notifications/read-all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("[200103] All notifications marked as read"));

        verify(notificationService, times(1)).markAllAsRead(USER_ID);
    }
}
