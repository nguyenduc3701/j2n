package com.example.j2n.auth_srv.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.example.j2n.auth_srv.repository.entity.PermissionEntity;
import com.example.j2n.auth_srv.repository.entity.RoleEntity;
import com.example.j2n.auth_srv.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import com.example.j2n.auth_srv.service.response.BaseResponse;
import java.util.List;
import java.util.Optional;

import com.example.j2n.auth_srv.constant.MessageEnum;
import com.example.j2n.auth_srv.repository.PermissionRepository;
import com.example.j2n.auth_srv.service.response.PermissionResponse;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private static final Logger log = LoggerFactory.getLogger(RoleService.class);
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public BaseResponse<List<String>> getPermissionsByRoleId(String roleId) {
        log.info("[AUTH-SRV] Get permissions by role id: {}", roleId);
        RoleEntity role = getRoleById(roleId);
        log.info("[AUTH-SRV] Get permissions by role id success");
        List<String> permissionNames = role.getPermissions().stream()
                .map(PermissionEntity::getName)
                .toList();
        log.info("[AUTH-SRV] Get permissions by role id success");
        return BaseResponse.success(permissionNames);
    }

    public RoleEntity getRoleById(String roleId) {
        log.info("[AUTH-SRV] Get role by id: {}", roleId);
        Optional<RoleEntity> role = roleRepository.findById(Long.parseLong(roleId));
        if (role.isEmpty()) {
            throw new IllegalArgumentException(MessageEnum.ROLE_NOT_FOUND.getMessage());
        }
        log.info("[AUTH-SRV] Get role by id success");
        return role.get();
    }

    public BaseResponse<List<PermissionResponse>> getPermissions() {
        log.info("[AUTH-SRV] Get permissions");
        List<PermissionEntity> permissions = permissionRepository.findAll();
        log.info("[AUTH-SRV] Get permissions success");
        return BaseResponse.success(mapPermissionEntityListToPermissionResponseList(permissions));
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
