package com.example.j2n.bff_srv.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.j2n.bff_srv.controller.request.LoginRequest;
import com.example.j2n.bff_srv.controller.request.RefreshTokenRequest;
import com.example.j2n.bff_srv.controller.request.RegisterRequest;
import com.example.j2n.bff_srv.service.AuthService;
import com.example.j2n.bff_srv.service.response.ClientLoginResponse;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.bff_srv.controller.request.SearchUserRequest;
import com.example.j2n.bff_srv.controller.request.CreateUserRequest;
import com.example.j2n.bff_srv.controller.request.UpdateUserRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/bff")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
   private final AuthService authService;

   @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   @Operation(summary = "Login", description = "Login")
   public ResponseEntity<BaseResponse<ClientLoginResponse>> login(@RequestBody LoginRequest request,
         HttpServletResponse response) {
      return ResponseEntity.ok(authService.login(request, response));
   }

   @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   @Operation(summary = "Register", description = "Register")
   public ResponseEntity<Object> register(@RequestBody RegisterRequest request) {
      return ResponseEntity.ok(authService.register(request));
   }

   @PostMapping(value = "/users/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   @Operation(summary = "Get list users", description = "Get list users")
   public ResponseEntity<Object> getListUsers(@RequestBody SearchUserRequest request) {
      return ResponseEntity.ok(authService.getListUsers(request));
   }

   @GetMapping(value = "/users/me", produces = MediaType.APPLICATION_JSON_VALUE)
   @Operation(summary = "Get user me", description = "Get user me")
   public ResponseEntity<Object> getUserMe() {
      return ResponseEntity.ok(authService.getUserMe());
   }

   @GetMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
   @Operation(summary = "Get user by id", description = "Get user by id")
   public ResponseEntity<Object> getUserById(@PathVariable String id) {
      return ResponseEntity.ok(authService.getUserById(id));
   }

   @PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   @Operation(summary = "Create user", description = "Create user")
   public ResponseEntity<Object> createUser(@RequestBody CreateUserRequest request) {
      return ResponseEntity.ok(authService.createUser(request));
   }

   @PutMapping(value = "/users/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   @Operation(summary = "Update user", description = "Update user")
   public ResponseEntity<Object> updateUser(@PathVariable String id, @RequestBody UpdateUserRequest request) {
      return ResponseEntity.ok(authService.updateUser(id, request));
   }

   @DeleteMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
   @Operation(summary = "Delete user", description = "Delete user")
   public ResponseEntity<Object> deleteUser(@PathVariable String id) {
      return ResponseEntity.ok(authService.deleteUser(id));
   }

   @GetMapping(value = "/roles", produces = MediaType.APPLICATION_JSON_VALUE)
   @Operation(summary = "Get roles", description = "Get roles")
   public ResponseEntity<Object> getRoles() {
      return ResponseEntity.ok(authService.getRoles());
   }

   @GetMapping(value = "/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
   @Operation(summary = "Get permissions", description = "Get permissions")
   public ResponseEntity<Object> getPermissions() {
      return ResponseEntity.ok(authService.getPermissions());
   }

   @GetMapping(value = "/roles/{roleId}/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
   @Operation(summary = "Get permissions by role id", description = "Get permissions by role id")
   public ResponseEntity<Object> getPermissionsByRoleId(@PathVariable String roleId) {
      return ResponseEntity.ok(authService.getPermissionsByRoleId(roleId));
   }

   @PostMapping(value = "/logout", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   @Operation(summary = "Logout", description = "Logout")
   public ResponseEntity<Object> logout() {
      return ResponseEntity.ok(authService.logout());
   }
}
