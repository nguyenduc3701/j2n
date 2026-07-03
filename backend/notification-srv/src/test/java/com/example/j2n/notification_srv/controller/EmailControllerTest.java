package com.example.j2n.notification_srv.controller;

import com.example.j2n.notification_srv.controller.request.SendEmailRequest;
import com.example.j2n.notification_srv.filter.InternalAuthFilter;
import com.example.j2n.notification_srv.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private InternalAuthFilter internalAuthFilter;

    @MockitoBean
    private org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void sendEmail_Success() throws Exception {
        // Arrange
        SendEmailRequest request = SendEmailRequest.builder()
                .emails(List.of("test@example.com"))
                .build();
        doNothing().when(emailService).sendManualBillEmails(any(SendEmailRequest.class));

        // Act & Assert
        mockMvc.perform(post("/emails/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("[200200] Success"));

        verify(emailService, times(1)).sendManualBillEmails(any(SendEmailRequest.class));
    }

    @Test
    void sendEmail_ValidationError_ThrowsBadRequest() throws Exception {
        // Arrange
        SendEmailRequest request = SendEmailRequest.builder()
                .emails(List.of("test@example.com"))
                .build();
        doThrow(new IllegalArgumentException("Emails list cannot be empty"))
                .when(emailService).sendManualBillEmails(any(SendEmailRequest.class));

        // Act & Assert
        mockMvc.perform(post("/emails/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("[500509] Emails list cannot be empty"));

        verify(emailService, times(1)).sendManualBillEmails(any(SendEmailRequest.class));
    }
}
