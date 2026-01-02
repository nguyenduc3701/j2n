package com.example.j2n.auth_srv.controllers;

import io.swagger.v3.oas.annotations.Operation;
import com.example.j2n.auth_srv.service.PermissionService;
import com.example.j2n.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.example.j2n.auth_srv.service.response.PermissionResponse;

@RestController
@RequestMapping("/api/auth/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @Operation(summary = "Get all permissions", description = "Get all permissions")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<PermissionResponse>>> getPermissions() {
        return ResponseEntity.ok(permissionService.getPermissions());
    }

    @Operation(summary = "Get permissions by role id", description = "Get permissions by role id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<String>>> getPermissionsByRoleId(@PathVariable String id) {
        return ResponseEntity.ok(permissionService.getPermissionsByRoleId(id));
    }
}
