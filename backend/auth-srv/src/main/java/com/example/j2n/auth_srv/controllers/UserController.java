package com.example.j2n.auth_srv.controllers;

import com.example.j2n.swagger.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import com.example.j2n.auth_srv.service.UserService;
import com.example.j2n.auth_srv.service.response.DeleteUserReponse;
import com.example.j2n.auth_srv.service.response.UserItemResponse;
import com.example.j2n.auth_srv.service.response.UserResponse;
import com.example.j2n.auth_srv.controllers.requests.CreateUserRequest;
import com.example.j2n.auth_srv.controllers.requests.UpdateUserRequest;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.j2n.auth_srv.controllers.requests.SearchUsersRequest;
import com.example.j2n.auth_srv.constant.PermissionConst;
import com.example.j2n.auth_srv.constant.MessageEnum;

@RestController
@RequestMapping("/auth/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasAuthority('CAN_VIEW_MANAGEMENT_PAGE')")
    @J2NApiRole(PermissionConst.CAN_VIEW_MANAGEMENT_PAGE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = "Success", examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            }),
            @J2NApiResponse(httpCode = 401, description = "Unauthorized", examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.TOKEN_INVALID),
                    @J2NApiExample(status = MessageEnum.MessageConstants.TOKEN_EXPIRED)
            })
    })
    @Operation(summary = "List users", description = "List users")
    @PostMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<UserResponse>> listUsers(@RequestBody SearchUsersRequest request) {
        return ResponseEntity.ok(userService.searchUsers(request));
    }

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = "Success", examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            }),
            @J2NApiResponse(httpCode = 401, description = "Unauthorized", examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.TOKEN_INVALID),
                    @J2NApiExample(status = MessageEnum.MessageConstants.TOKEN_EXPIRED)
            })
    })
    @Operation(summary = "Get current user", description = "Get current user")
    public ResponseEntity<BaseResponse<UserResponse.UserItem>> getMe() {
        return ResponseEntity.ok(userService.getMe());
    }

    @PreAuthorize("hasAuthority('CAN_VIEW_MANAGEMENT_PAGE')")
    @J2NApiRole(PermissionConst.CAN_VIEW_MANAGEMENT_PAGE)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = "Success", examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            }),
            @J2NApiResponse(httpCode = 400, description = "Bad Request", examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.USER_NOT_FOUND)
            }),
            @J2NApiResponse(httpCode = 401, description = "Unauthorized", examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.TOKEN_INVALID),
                    @J2NApiExample(status = MessageEnum.MessageConstants.TOKEN_EXPIRED)
            })
    })
    @Operation(summary = "Get user by id", description = "Get user by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<UserResponse.UserItem>> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PreAuthorize("hasAuthority('CAN_CREATE_USER')")
    @J2NApiRole(PermissionConst.CAN_CREATE_USER)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = "Success", examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.CREATE_USER_SUCCESS)
            }),
            @J2NApiResponse(httpCode = 400, description = "Bad Request", examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.FIELD_REQUIRED, args = { "username" }),
                    @J2NApiExample(status = MessageEnum.MessageConstants.FIELD_EXISTED, args = { "email" })
            }),
            @J2NApiResponse(httpCode = 401, description = "Unauthorized", examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.TOKEN_INVALID),
                    @J2NApiExample(status = MessageEnum.MessageConstants.TOKEN_EXPIRED)
            })
    })
    @Operation(summary = "Create user", description = "Create user")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<UserItemResponse>> createUser(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PreAuthorize("hasAuthority('CAN_UPDATE_USER')")
    @J2NApiRole(PermissionConst.CAN_UPDATE_USER)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = "Success", examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.UPDATE_USER_SUCCESS)
            }),
            @J2NApiResponse(httpCode = 400, description = "Bad Request", examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.USER_NOT_FOUND),
                    @J2NApiExample(status = MessageEnum.MessageConstants.ROLE_NOT_ALLOW_ACTION)
            }),
            @J2NApiResponse(httpCode = 401, description = "Unauthorized", examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.TOKEN_INVALID),
                    @J2NApiExample(status = MessageEnum.MessageConstants.TOKEN_EXPIRED)
            })
    })
    @Operation(summary = "Update user", description = "Update user")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<UserItemResponse>> updateUser(@PathVariable String id,
            @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PreAuthorize("hasAuthority('CAN_DELETE_USER')")
    @J2NApiRole(PermissionConst.CAN_DELETE_USER)
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = "Success", examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.DELETE_USER_SUCCESS)
            }),
            @J2NApiResponse(httpCode = 400, description = "Bad Request", examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.USER_NOT_FOUND),
                    @J2NApiExample(status = MessageEnum.MessageConstants.ROLE_NOT_ALLOW_ACTION)
            }),
            @J2NApiResponse(httpCode = 401, description = "Unauthorized", examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.TOKEN_INVALID),
                    @J2NApiExample(status = MessageEnum.MessageConstants.TOKEN_EXPIRED)
            })
    })
    @Operation(summary = "Delete user", description = "Delete user")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<DeleteUserReponse>> deleteUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }
}
