package com.example.j2n.auth_srv.controllers;

import com.example.j2n.auth_srv.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/permission")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;
}
