package com.example.j2n.bff_srv.auth_srv.controllers;

import com.example.j2n.bff_srv.auth_srv.controllers.requests.CreateUserRequest;
import com.example.j2n.bff_srv.auth_srv.controllers.requests.UpdateUserRequest;
import com.example.j2n.bff_srv.auth_srv.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getUser(){
        return ResponseEntity.ok(userService.getUser());
    }
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getUserById(@PathVariable String userId){
        return ResponseEntity.ok(userService.getUserById(userId));
    }
    @GetMapping(value = "/{role_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getUserByRoleId(@PathVariable String roleId){
        return ResponseEntity.ok(userService.getUserByRoleId(roleId));
    }
    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createUser(@RequestBody CreateUserRequest request){
        return ResponseEntity.ok(userService.createUser(request));
    }
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createUser(@PathVariable String userId,@RequestBody UpdateUserRequest request){
        return ResponseEntity.ok(userService.updateUser(userId,request));
    }
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteUser(@PathVariable String userId){
        return ResponseEntity.ok(userService.deleteUser(userId));
    }
}
