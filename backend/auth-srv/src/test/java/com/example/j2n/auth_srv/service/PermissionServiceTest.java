package com.example.j2n.auth_srv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.j2n.auth_srv.repository.PermissionRepository;
import com.example.j2n.auth_srv.repository.RoleRepository;
import com.example.j2n.auth_srv.repository.entity.PermissionEntity;
import com.example.j2n.auth_srv.repository.entity.RoleEntity;
import com.example.j2n.auth_srv.service.response.BaseResponse;
import com.example.j2n.auth_srv.service.response.PermissionResponse;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private PermissionService permissionService;

    @Test
    void getPermissionsByRoleId_ShouldReturnPermissions_WhenRoleExists() {
        // Arrange
        String roleId = "1";
        RoleEntity role = new RoleEntity();
        role.setId(1L);

        PermissionEntity permission = new PermissionEntity();
        permission.setName("READ");
        role.setPermissions(Set.of(permission));

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        // Act
        BaseResponse<List<String>> response = permissionService.getPermissionsByRoleId(roleId);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getData().size());
        assertEquals("READ", response.getData().get(0));
    }

    @Test
    void getPermissionsByRoleId_ShouldThrowException_WhenRoleNotFound() {
        // Arrange
        String roleId = "1";
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> permissionService.getPermissionsByRoleId(roleId));
    }

    @Test
    void getRoleById_ShouldReturnRole_WhenRoleExists() {
        // Arrange
        String roleId = "1";
        RoleEntity role = new RoleEntity();
        role.setId(1L);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        // Act
        RoleEntity result = permissionService.getRoleById(roleId);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getRoleById_ShouldThrowException_WhenRoleNotFound() {
        // Arrange
        String roleId = "1";
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> permissionService.getRoleById(roleId));
    }

    @Test
    void getPermissions_ShouldReturnAllPermissions() {
        // Arrange
        PermissionEntity permission = new PermissionEntity();
        permission.setId(1L);
        permission.setName("READ");
        permission.setDescription("Read permission");

        when(permissionRepository.findAll()).thenReturn(List.of(permission));

        // Act
        BaseResponse<List<PermissionResponse>> response = permissionService.getPermissions();

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getData().size());
        assertEquals("READ", response.getData().get(0).getName());
    }
}
