package com.example.j2n.notification_srv.controller;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.dto.SimpleBaseMessage;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.notification_srv.controller.request.SendEmailRequest;
import com.example.j2n.notification_srv.service.EmailService;
import com.example.j2n.swagger.annotation.J2NApiResponse;
import com.example.j2n.swagger.annotation.J2NApiResponses;
import com.example.j2n.utils.ResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/emails")
@RequiredArgsConstructor
@Tag(name = "Email Controller", description = "Endpoints for managing and sending emails")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    @Operation(summary = "Send an email", description = "Send a manual email to a specific address")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public ResponseEntity<BaseResponse<Object>> sendEmail(@Valid @RequestBody SendEmailRequest request) {
        try {
            emailService.sendManualBillEmails(request);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ResponseFactory.error(new SimpleBaseMessage(
                    BaseMessageEnum.BAD_REQUEST.getCode(),
                    BaseMessageEnum.BAD_REQUEST.getHttpStatus(),
                    e.getMessage()
            )));
        }

        return ResponseEntity.ok(ResponseFactory.success(null));
    }
}
