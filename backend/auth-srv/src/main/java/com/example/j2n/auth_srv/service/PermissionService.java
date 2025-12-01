package com.example.j2n.auth_srv.service;

import org.springframework.stereotype.Service;
import com.example.j2n.auth_srv.repository.entity.PermissionEntity;
import com.example.j2n.auth_srv.repository.entity.RoleEntity;
import com.example.j2n.auth_srv.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import com.example.j2n.auth_srv.service.response.BaseResponse;
import java.util.List;
import java.util.Optional;

import com.example.j2n.auth_srv.constant.MessageEnum;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final RoleRepository roleRepository;

    public BaseResponse<List<String>> getPermissionsByRoleId(String roleId) {
        RoleEntity role = getRoleById(roleId);
        List<String> permissionNames = role.getPermissions().stream()
                .map(PermissionEntity::getName)
                .toList();
        return BaseResponse.success(permissionNames);
    }

    public RoleEntity getRoleById(String roleId) {
        Optional<RoleEntity> role = roleRepository.findById(Long.parseLong(roleId));
        if (role.isEmpty()) {
            throw new IllegalArgumentException(MessageEnum.ROLE_NOT_FOUND.getMessage());
        }
        return role.get();
    }
}
