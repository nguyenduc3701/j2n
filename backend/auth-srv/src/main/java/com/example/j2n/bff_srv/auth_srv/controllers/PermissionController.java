package com.example.j2n.bff_srv.auth_srv.controllers;

import com.example.j2n.bff_srv.auth_srv.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/permission")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getPermissions(){
        return ResponseEntity.ok(permissionService.getPermissions());
    }
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getPermissionByRoleId(@PathVariable String roleId){
        return ResponseEntity.ok(permissionService.getPermissionByRoleId(roleId));
    }
}
