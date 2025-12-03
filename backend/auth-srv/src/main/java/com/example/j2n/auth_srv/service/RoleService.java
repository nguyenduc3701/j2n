package com.example.j2n.auth_srv.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import com.example.j2n.auth_srv.repository.RoleRepository;
import lombok.RequiredArgsConstructor;

import com.example.j2n.auth_srv.service.response.BaseResponse;
import com.example.j2n.auth_srv.service.response.RoleResponse;
import com.example.j2n.auth_srv.repository.entity.RoleEntity;

@Service
@RequiredArgsConstructor
public class RoleService {
    private static final Logger log = LoggerFactory.getLogger(RoleService.class);
    private final RoleRepository roleRepository;

    public BaseResponse<List<RoleResponse>> getRoles() {
        log.info("[AUTH-SRV] Get roles");
        List<RoleEntity> roles = roleRepository.findAll();
        log.info("[AUTH-SRV] Get roles success");
        return BaseResponse.success(mapRoleEntityListToRoleResponseList(roles));
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
