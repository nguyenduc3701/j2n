package com.example.j2n.auth_srv.service;

import com.example.j2n.dto.BaseResponse;
import com.example.j2n.utils.ResponseFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import com.example.j2n.auth_srv.repository.RoleRepository;
import lombok.RequiredArgsConstructor;

import com.example.j2n.auth_srv.service.response.RoleResponse;
import com.example.j2n.auth_srv.repository.entity.RoleEntity;
import com.example.j2n.aspect.LogAround;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    @LogAround(message = "Getting all roles")
    public BaseResponse<List<RoleResponse>> getRoles() {
        List<RoleEntity> roles = roleRepository.findAll();
        return ResponseFactory.success(mapRoleEntityListToRoleResponseList(roles));
    }

    private List<RoleResponse> mapRoleEntityListToRoleResponseList(List<RoleEntity> roleEntities) {
        return roleEntities.stream()
                .map(this::mapRoleEntityToRoleResponse)
                .toList();
    }

    private RoleResponse mapRoleEntityToRoleResponse(RoleEntity roleEntity) {
        RoleResponse response = new RoleResponse();
        response.setId(roleEntity.getId());
        response.setName(roleEntity.getName());
        response.setDescription(roleEntity.getDescription());
        return response;
    }
}
