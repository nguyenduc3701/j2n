package com.example.j2n.auth_srv.controllers;

import io.swagger.v3.oas.annotations.Operation;
import com.example.j2n.swagger.annotation.*;
import com.example.j2n.auth_srv.constant.MessageEnum;
import com.example.j2n.auth_srv.service.PermissionService;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.example.j2n.auth_srv.service.response.PermissionResponse;

@RestController
@RequestMapping("/auth/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @Operation(summary = "Get all permissions", description = "Get all permissions")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = "Success", examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            })
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<PermissionResponse>>> getPermissions() {
        return ResponseEntity.ok(permissionService.getPermissions());
    }

    @Operation(summary = "Get permissions by role id", description = "Get permissions by role id")
    @J2NApiResponses({
            @J2NApiResponse(httpCode = 200, description = "Success", examples = {
                    @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
            }),
            @J2NApiResponse(httpCode = 400, description = "Bad Request", examples = {
                    @J2NApiExample(status = MessageEnum.MessageConstants.ROLE_NOT_FOUND)
            })
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<List<String>>> getPermissionsByRoleId(@PathVariable String id) {
        return ResponseEntity.ok(permissionService.getPermissionsByRoleId(id));
    }
}
