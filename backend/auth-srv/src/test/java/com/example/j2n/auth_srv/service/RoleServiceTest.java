package com.example.j2n.auth_srv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.j2n.auth_srv.repository.RoleRepository;
import com.example.j2n.auth_srv.repository.entity.RoleEntity;
import com.example.j2n.auth_srv.service.response.BaseResponse;
import com.example.j2n.auth_srv.service.response.RoleResponse;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    void getRoles_ShouldReturnAllRoles() {
        // Arrange
        RoleEntity role = new RoleEntity();
        role.setId(1L);
        role.setName("ADMIN");
        role.setDescription("Administrator role");

        when(roleRepository.findAll()).thenReturn(List.of(role));

        // Act
        BaseResponse<List<RoleResponse>> response = roleService.getRoles();

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getData().size());
        assertEquals("ADMIN", response.getData().get(0).getName());
    }
}
