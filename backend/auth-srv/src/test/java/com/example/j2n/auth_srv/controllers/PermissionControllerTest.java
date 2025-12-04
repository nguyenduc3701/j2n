package com.example.j2n.auth_srv.controllers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.j2n.auth_srv.service.PermissionService;
import com.example.j2n.auth_srv.service.response.BaseResponse;
import com.example.j2n.auth_srv.service.response.PermissionResponse;

@ExtendWith(MockitoExtension.class)
class PermissionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private PermissionController permissionController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(permissionController).build();
    }

    @Test
    void getPermissions_ShouldReturnAllPermissions() throws Exception {
        // Arrange
        PermissionResponse permission1 = new PermissionResponse();
        permission1.setName("READ");
        PermissionResponse permission2 = new PermissionResponse();
        permission2.setName("WRITE");

        List<PermissionResponse> permissions = Arrays.asList(permission1, permission2);
        when(permissionService.getPermissions()).thenReturn(BaseResponse.success(permissions));

        // Act & Assert
        mockMvc.perform(get("/api/auth/permissions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("READ"))
                .andExpect(jsonPath("$.data[1].name").value("WRITE"));
    }

    @Test
    void getPermissionsByRoleId_ShouldReturnPermissions() throws Exception {
        // Arrange
        String roleId = "1";
        List<String> permissions = Arrays.asList("READ", "WRITE", "DELETE");
        when(permissionService.getPermissionsByRoleId(anyString())).thenReturn(BaseResponse.success(permissions));

        // Act & Assert
        mockMvc.perform(get("/api/auth/permissions/{id}", roleId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0]").value("READ"))
                .andExpect(jsonPath("$.data[1]").value("WRITE"))
                .andExpect(jsonPath("$.data[2]").value("DELETE"));
    }
}
