package com.example.j2n.notification_srv.service;

import com.example.j2n.notification_srv.controller.request.SendEmailRequest;
import com.example.j2n.notification_srv.messaging.event.BillNotificationEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private EmailService emailService;

    private static final String FROM_EMAIL = "sender@example.com";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", FROM_EMAIL);
    }

    @Test
    void sendBillEmail_Success() {
        // Arrange
        BillNotificationEvent.MemberInfo member = BillNotificationEvent.MemberInfo.builder()
                .userId("user-1")
                .fullName("John Doe")
                .email("john.doe@example.com")
                .build();

        BillNotificationEvent event = BillNotificationEvent.builder()
                .billId("bill-1")
                .roomNumber("101")
                .billingMonth(5)
                .electricAmount(BigDecimal.valueOf(100000))
                .waterAmount(BigDecimal.valueOf(50000))
                .roomAmount(BigDecimal.valueOf(2000000))
                .serviceAmount(BigDecimal.valueOf(150000))
                .totalAmount(BigDecimal.valueOf(2300000))
                .electricityUsage(100)
                .qrPaymentUrl("http://qr.example.com")
                .build();

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("bill-template"), any(Context.class))).thenReturn("<html>Test Email Content</html>");

        // Act
        emailService.sendBillEmail(member, event);

        // Assert
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
        verify(templateEngine, times(1)).process(eq("bill-template"), any(Context.class));
    }

    @Test
    void sendBillEmail_Failure_ThrowsRuntimeException() throws Exception {
        // Arrange
        BillNotificationEvent.MemberInfo member = BillNotificationEvent.MemberInfo.builder()
                .userId("user-1")
                .fullName("John Doe")
                .email("john.doe@example.com")
                .build();

        BillNotificationEvent event = BillNotificationEvent.builder()
                .billId("bill-1")
                .roomNumber("101")
                .billingMonth(5)
                .electricAmount(BigDecimal.valueOf(100000))
                .waterAmount(BigDecimal.valueOf(50000))
                .roomAmount(BigDecimal.valueOf(2000000))
                .serviceAmount(BigDecimal.valueOf(150000))
                .totalAmount(BigDecimal.valueOf(2300000))
                .electricityUsage(100)
                .qrPaymentUrl("http://qr.example.com")
                .build();

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MessagingException("From error")).when(mimeMessage).setFrom(any(jakarta.mail.Address.class));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendBillEmail(member, event);
        });

        assertTrue(exception.getMessage().contains("Failed to send email to: john.doe@example.com"));
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void sendManualBillEmails_EmptyEmailsList_ThrowsIllegalArgumentException() {
        // Arrange
        SendEmailRequest request = SendEmailRequest.builder()
                .emails(Collections.emptyList())
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            emailService.sendManualBillEmails(request);
        });

        assertEquals("Emails list cannot be empty", exception.getMessage());
    }

    @Test
    void sendManualBillEmails_NullEmailsList_ThrowsIllegalArgumentException() {
        // Arrange
        SendEmailRequest request = SendEmailRequest.builder()
                .emails(null)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            emailService.sendManualBillEmails(request);
        });

        assertEquals("Emails list cannot be empty", exception.getMessage());
    }

    @Test
    void sendManualBillEmails_Success_WithEvent() {
        // Arrange
        List<String> emails = List.of("client1@example.com", "client2@example.com");
        BillNotificationEvent event = BillNotificationEvent.builder()
                .billId("bill-1")
                .roomNumber("202")
                .billingMonth(6)
                .electricAmount(BigDecimal.valueOf(120000))
                .waterAmount(BigDecimal.valueOf(60000))
                .roomAmount(BigDecimal.valueOf(2500000))
                .serviceAmount(BigDecimal.valueOf(200000))
                .totalAmount(BigDecimal.valueOf(2880000))
                .electricityUsage(120)
                .qrPaymentUrl("http://qr.example.com/202")
                .build();

        SendEmailRequest request = SendEmailRequest.builder()
                .emails(emails)
                .event(event)
                .build();

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("bill-template"), any(Context.class))).thenReturn("<html>HTML Content</html>");

        // Act
        emailService.sendManualBillEmails(request);

        // Assert
        verify(mailSender, times(2)).createMimeMessage();
        verify(mailSender, times(2)).send(any(MimeMessage.class));
        verify(templateEngine, times(2)).process(eq("bill-template"), any(Context.class));
    }

    @Test
    void sendManualBillEmails_Success_WithNullEvent_UsesDefaultDummy() {
        // Arrange
        List<String> emails = List.of("client@example.com");
        SendEmailRequest request = SendEmailRequest.builder()
                .emails(emails)
                .event(null)
                .build();

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("bill-template"), any(Context.class))).thenReturn("<html>HTML Content</html>");

        // Act
        emailService.sendManualBillEmails(request);

        // Assert
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
        verify(templateEngine, times(1)).process(eq("bill-template"), any(Context.class));
    }
}
