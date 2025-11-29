package com.example.j2n.bff_srv.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.j2n.bff_srv.service.UserService;

@RestController
@RequestMapping("api/bff/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
   private final UserService userService;

   @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<Object> getUsers() {
      return ResponseEntity.ok(userService.getUsers());
   }
}
