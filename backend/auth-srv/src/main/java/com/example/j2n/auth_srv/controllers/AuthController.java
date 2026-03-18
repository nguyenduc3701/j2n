package com.example.j2n.auth_srv.controllers;

import com.example.j2n.auth_srv.controllers.requests.LoginRequest;
import com.example.j2n.auth_srv.controllers.requests.RegisterRequest;
import com.example.j2n.auth_srv.service.AuthService;
import com.example.j2n.auth_srv.service.response.LoginResponse;
import com.example.j2n.auth_srv.service.response.UserItemResponse;
import com.example.j2n.auth_srv.controllers.requests.ForgotPasswordRequest;
import com.example.j2n.swagger.annotation.*;
import com.example.j2n.auth_srv.constant.MessageEnum;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Login", description = "Login with username and password")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = "Success", examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            }),
            @J2NApiResponse(httpCode = 400, description = "Bad Request", examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.USER_NOT_FOUND),
                    @J2NApiExample(status = MessageEnum.MessageConstants.INVALID_CREDENTIALS)
            })
    })
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Register", description = "Register with username and password")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = "Success", examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            }),
            @J2NApiResponse(httpCode = 400, description = "Bad Request", examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.FIELD_REQUIRED, args = {
                            "Username" }),
                    @J2NApiExample(status = MessageEnum.MessageConstants.FIELD_EXISTED, args = {
                            "Email" })
            })
    })
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<UserItemResponse>> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(summary = "Refresh token", description = "Refresh token")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = "Success", examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            }),
            @J2NApiResponse(httpCode = 401, description = "Unauthorized", examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.TOKEN_INVALID),
                    @J2NApiExample(status = MessageEnum.MessageConstants.TOKEN_EXPIRED)
            })
    })
    @PostMapping(value = "/refresh-token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<LoginResponse>> refreshToken(
            @RequestHeader("X-Refresh-Token") String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @Operation(summary = "Logout", description = "Logout from system")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = "Success", examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            }),
            @J2NApiResponse(httpCode = 401, description = "Unauthorized", examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.TOKEN_INVALID)
            })
    })
    @PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<String>> logout(@RequestHeader("Authorization") String authorization,
            @RequestHeader("X-Refresh-Token") String refreshToken) {
        return ResponseEntity.ok(authService.logout(authorization, refreshToken));
    }

    @Operation(summary = "Forgot password", description = "Forgot password request")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = "Success", examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    @PostMapping(value = "/forgot-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<String>> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }
}
