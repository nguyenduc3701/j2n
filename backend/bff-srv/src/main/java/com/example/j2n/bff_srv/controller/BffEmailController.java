package com.example.j2n.bff_srv.controller;

import com.example.j2n.bff_srv.service.EmailService;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.swagger.annotation.J2NApiResponse;
import com.example.j2n.swagger.annotation.J2NApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bff/emails")
@RequiredArgsConstructor
@Tag(name = "Email BFF Controller", description = "BFF Endpoints for sending emails")
public class BffEmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    @Operation(summary = "Send an email", description = "Send a manual email through the notification service")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS)
    })
    public ResponseEntity<Object> sendEmail(@RequestBody Object request) {
        return ResponseEntity.ok(emailService.sendEmail(request));
    }
}
