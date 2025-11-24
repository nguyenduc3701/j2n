package com.example.j2n.bff_srv.auth_srv.controllers;

import com.example.j2n.bff_srv.auth_srv.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getRoles() {
        return ResponseEntity.ok(roleService.getRoles());
    }

    @GetMapping(value = "/{id}/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getPermissionsByRoleId(@PathVariable String roleId) {
        return ResponseEntity.ok(roleService.getPermissionsByRoleId(roleId));
    }
}
