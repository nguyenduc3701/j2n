package com.example.j2n.auth_srv.controllers;

import com.example.j2n.auth_srv.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.j2n.auth_srv.service.response.BaseResponse;
import java.util.List;
import com.example.j2n.auth_srv.service.response.PermissionResponse;

@RestController
@RequestMapping("/api/auth/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<PermissionResponse>>> getPermissions() {
        return ResponseEntity.ok(permissionService.getPermissions());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<String>>> getPermissionsByRoleId(@PathVariable String id) {
        return ResponseEntity.ok(permissionService.getPermissionsByRoleId(id));
    }
}
