package com.example.j2n.auth_srv.controllers;

import io.swagger.v3.oas.annotations.Operation;
import com.example.j2n.auth_srv.service.UserService;
import com.example.j2n.auth_srv.service.response.UserResponse;
import com.example.j2n.auth_srv.controllers.requests.CreateUserRequest;
import com.example.j2n.auth_srv.controllers.requests.UpdateUserRequest;
import com.example.j2n.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.j2n.auth_srv.controllers.requests.SearchUsersRequest;

@RestController
@RequestMapping("/auth/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasAuthority('CAN_VIEW_MANAGEMENT_PAGE')")
    @Operation(summary = "List users", description = "List users")
    @PostMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<UserResponse>> listUsers(@RequestBody SearchUsersRequest request) {
        return ResponseEntity.ok(userService.searchUsers(request));
    }

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get current user", description = "Get current user")
    public ResponseEntity<BaseResponse<UserResponse.UserItem>> getMe() {
        return ResponseEntity.ok(userService.getMe());
    }

    @PreAuthorize("hasAuthority('CAN_VIEW_MANAGEMENT_PAGE')")
    @Operation(summary = "Get user by id", description = "Get user by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<UserResponse.UserItem>> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PreAuthorize("hasAuthority('CAN_CREATE_USER')")
    @Operation(summary = "Create user", description = "Create user")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createUser(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PreAuthorize("hasAuthority('CAN_UPDATE_USER')")
    @Operation(summary = "Update user", description = "Update user")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateUser(@PathVariable String id, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PreAuthorize("hasAuthority('CAN_DELETE_USER')")
    @Operation(summary = "Delete user", description = "Delete user")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }
}
