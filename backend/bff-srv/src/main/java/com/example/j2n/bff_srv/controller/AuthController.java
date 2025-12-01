package com.example.j2n.bff_srv.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.j2n.bff_srv.controller.request.LoginRequest;
import com.example.j2n.bff_srv.controller.request.RegisterRequest;
import com.example.j2n.bff_srv.service.AuthService;

@RestController
@RequestMapping("api/bff")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
   private final AuthService authService;

   @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<Object> login(@RequestBody LoginRequest request) {
      return ResponseEntity.ok(authService.login(request));
   }

   @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<Object> register(@RequestBody RegisterRequest request) {
      return ResponseEntity.ok(authService.register(request));
   }

   @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<Object> getListUsers() {
      return ResponseEntity.ok(authService.getListUsers());
   }

   @GetMapping(value = "/users/me", produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<Object> getUserMe() {
      return ResponseEntity.ok(authService.getUserMe());
   }

   @GetMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<Object> getUserById(@PathVariable String id) {
      return ResponseEntity.ok(authService.getUserById(id));
   }
}
