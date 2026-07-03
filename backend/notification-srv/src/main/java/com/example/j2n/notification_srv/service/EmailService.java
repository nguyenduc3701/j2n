package com.example.j2n.notification_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.notification_srv.messaging.event.BillNotificationEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.text.NumberFormat;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @LogAround(message = "Send bill email via rabbitmq event")
    public void sendBillEmail(BillNotificationEvent.MemberInfo member, BillNotificationEvent event) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(member.getEmail());
            helper.setSubject(buildSubject(event));

            String htmlContent = buildEmailContent(member, event);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("[EMAIL-SERVICE] Bill email sent successfully to: {}", member.getEmail());
        } catch (MessagingException e) {
            log.error("[EMAIL-SERVICE] Failed to send bill email to: {}, error: {}", member.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to send email to: " + member.getEmail(), e);
        }
    }

    @LogAround(message = "Send manual bill emails via API")
    public void sendManualBillEmails(com.example.j2n.notification_srv.controller.request.SendEmailRequest request) {
        if (request.getEmails() == null || request.getEmails().isEmpty()) {
            throw new IllegalArgumentException("Emails list cannot be empty");
        }

        BillNotificationEvent eventData = request.getEvent();
        if (eventData == null) {
            // Provide a default dummy event for testing
            eventData = BillNotificationEvent.builder()
                    .roomNumber("TEST-101")
                    .billingMonth(1)
                    .electricAmount(new java.math.BigDecimal("150000"))
                    .waterAmount(new java.math.BigDecimal("50000"))
                    .roomAmount(new java.math.BigDecimal("2000000"))
                    .serviceAmount(new java.math.BigDecimal("100000"))
                    .totalAmount(new java.math.BigDecimal("2300000"))
                    .electricityUsage(50)
                    .qrPaymentUrl("https://upload.wikimedia.org/wikipedia/commons/d/d0/QR_code_for_mobile_English_Wikipedia.svg")
                    .build();
        }

        for (String email : request.getEmails()) {
            BillNotificationEvent.MemberInfo member =
                    new BillNotificationEvent.MemberInfo(null, "Valued Customer", email);
            sendBillEmail(member, eventData);
        }
    }



    private String buildSubject(BillNotificationEvent event) {
        return String.format("[J2N] Room Bill %s - Month %d",
                event.getRoomNumber(), event.getBillingMonth());
    }

    private String buildEmailContent(BillNotificationEvent.MemberInfo member, BillNotificationEvent event) {
        NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

        Context context = new Context();
        context.setVariable("memberName", member.getFullName() != null ? member.getFullName() : "Valued Customer");
        context.setVariable("roomNumber", event.getRoomNumber());
        context.setVariable("billingMonth", event.getBillingMonth());
        context.setVariable("electricityUsage", event.getElectricityUsage());
        context.setVariable("electricAmount", currencyFormat.format(event.getElectricAmount()));
        context.setVariable("waterAmount", currencyFormat.format(event.getWaterAmount()));
        context.setVariable("roomAmount", currencyFormat.format(event.getRoomAmount()));
        context.setVariable("serviceAmount", currencyFormat.format(event.getServiceAmount()));
        context.setVariable("totalAmount", currencyFormat.format(event.getTotalAmount()));
        context.setVariable("qrPaymentUrl", event.getQrPaymentUrl());

        return templateEngine.process("bill-template", context);
    }
}
