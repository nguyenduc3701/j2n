package com.example.j2n.auth_srv.service;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.utils.ResponseFactory;
import org.springframework.stereotype.Service;
import com.example.j2n.auth_srv.repository.entity.PermissionEntity;
import com.example.j2n.auth_srv.repository.entity.RoleEntity;
import com.example.j2n.auth_srv.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import com.example.j2n.auth_srv.constant.MessageEnum;
import com.example.j2n.aspect.LogAround;

import java.util.List;
import java.util.Optional;

import com.example.j2n.auth_srv.repository.PermissionRepository;
import com.example.j2n.auth_srv.service.response.PermissionResponse;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @LogAround(message = "Getting permissions by role ID")
    public BaseResponse<List<String>> getPermissionsByRoleId(String roleId) {
        RoleEntity role = getRoleById(roleId);
        List<String> permissionNames = role.getPermissions().stream()
                .map(PermissionEntity::getName)
                .toList();
        return ResponseFactory.success(permissionNames);
    }

    @LogAround(message = "Getting role by ID")
    public RoleEntity getRoleById(String roleId) {
        Optional<RoleEntity> role = roleRepository.findById(Long.parseLong(roleId));
        if (role.isEmpty()) {
            throw new IllegalArgumentException(MessageEnum.ROLE_NOT_FOUND.getMessage());
        }
        return role.get();
    }

    @LogAround(message = "Getting all permissions")
    public BaseResponse<List<PermissionResponse>> getPermissions() {
        List<PermissionEntity> permissions = permissionRepository.findAll();
        return ResponseFactory.success(mapPermissionEntityListToPermissionResponseList(permissions));
    }

    private List<PermissionResponse> mapPermissionEntityListToPermissionResponseList(
            List<PermissionEntity> permissionEntities) {
        return permissionEntities.stream()
                .map(this::mapPermissionEntityToPermissionResponse)
                .toList();
    }

    private PermissionResponse mapPermissionEntityToPermissionResponse(PermissionEntity permissionEntity) {
        PermissionResponse response = new PermissionResponse();
        response.setId(permissionEntity.getId());
        response.setName(permissionEntity.getName());
        response.setDescription(permissionEntity.getDescription());
        return response;
    }
}
