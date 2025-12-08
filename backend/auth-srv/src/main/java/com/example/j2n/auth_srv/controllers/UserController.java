package com.example.j2n.auth_srv.controllers;

import com.example.j2n.auth_srv.service.UserService;
import com.example.j2n.auth_srv.service.response.BaseResponse;
import com.example.j2n.auth_srv.service.response.UserResponse;
import com.example.j2n.auth_srv.controllers.requests.CreateUserRequest;
import com.example.j2n.auth_srv.controllers.requests.UpdateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/auth/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasAuthority('CAN_VIEW_MANAGEMENT_PAGE')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<UserResponse>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<UserResponse.UserItem>> getMe() {
        return ResponseEntity.ok(userService.getMe());
    }

    @PreAuthorize("hasAuthority('CAN_VIEW_MANAGEMENT_PAGE')")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<UserResponse.UserItem>> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PreAuthorize("hasAuthority('CAN_CREATE_USER')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createUser(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PreAuthorize("hasAuthority('CAN_UPDATE_USER')")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateUser(@PathVariable String id, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PreAuthorize("hasAuthority('CAN_DELETE_USER')")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }
}
