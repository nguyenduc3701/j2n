package com.example.j2n.bff_srv.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.j2n.bff_srv.controller.request.LoginRequest;
import com.example.j2n.bff_srv.controller.request.RegisterRequest;
import com.example.j2n.bff_srv.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.j2n.bff_srv.interceptor.JwtDecodeInterceptor;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
        private static final String LOGIN_ENDPOINT = "/api/bff/login";
        private static final String REGISTER_ENDPOINT = "/api/bff/register";
        private static final String USERS_ENDPOINT = "/api/bff/users";
        private static final String USER_ME_ENDPOINT = "/api/bff/users/me";
        private static final String USER_BY_ID_ENDPOINT = "/api/bff/users/{id}";

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private AuthService authService;

        @MockitoBean
        private JwtDecodeInterceptor jwtDecodeInterceptor;

        @Autowired
        private ObjectMapper objectMapper;

        @org.junit.jupiter.api.BeforeEach
        void setUp() throws Exception {
                when(jwtDecodeInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        }

        @Test
        @WithMockUser
        void login_ShouldReturnSuccess() throws Exception {
                // Arrange
                LoginRequest request = new LoginRequest();
                request.setUserName("testuser");
                request.setPassword("password");
                com.example.j2n.bff_srv.service.response.ClientLoginResponse clientLoginResponse = new com.example.j2n.bff_srv.service.response.ClientLoginResponse();
                clientLoginResponse.setAccessToken("test-token");
                com.example.j2n.dto.BaseResponse<com.example.j2n.bff_srv.service.response.ClientLoginResponse> expectedResponse = com.example.j2n.utils.ResponseFactory
                                .success(clientLoginResponse);

                when(authService.login(any(LoginRequest.class), any())).thenReturn(expectedResponse);

                // Act & Assert
                mockMvc.perform(post(LOGIN_ENDPOINT)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andDo(print())
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        void register_ShouldReturnSuccess() throws Exception {
                // Arrange
                RegisterRequest request = new RegisterRequest();
                request.setUserName("newuser");
                Map<String, String> expectedResponse = Collections.singletonMap("message", "success");

                when(authService.register(any(RegisterRequest.class))).thenReturn(expectedResponse);

                // Act & Assert
                mockMvc.perform(post(REGISTER_ENDPOINT)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        void getListUsers_ShouldReturnSuccess() throws Exception {
                // Arrange
                com.example.j2n.bff_srv.controller.request.SearchUserRequest request = new com.example.j2n.bff_srv.controller.request.SearchUserRequest();
                Map<String, String> expectedResponse = Collections.singletonMap("users", "[]");
                when(authService.getListUsers(any(com.example.j2n.bff_srv.controller.request.SearchUserRequest.class)))
                                .thenReturn(expectedResponse);

                // Act & Assert
                mockMvc.perform(post(USERS_ENDPOINT + "/list")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        void getUserMe_ShouldReturnSuccess() throws Exception {
                // Arrange
                Map<String, String> expectedResponse = Collections.singletonMap("user", "me");
                when(authService.getUserMe()).thenReturn(expectedResponse);

                // Act & Assert
                mockMvc.perform(get(USER_ME_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        void getUserById_ShouldReturnSuccess() throws Exception {
                // Arrange
                String userId = "123";
                Map<String, String> expectedResponse = Collections.singletonMap("user", "123");
                when(authService.getUserById(userId)).thenReturn(expectedResponse);

                // Act & Assert
                mockMvc.perform(get(USER_BY_ID_ENDPOINT, userId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        void createUser_ShouldReturnSuccess() throws Exception {
                // Arrange
                com.example.j2n.bff_srv.controller.request.CreateUserRequest request = new com.example.j2n.bff_srv.controller.request.CreateUserRequest();
                request.setUserName("newuser");
                Map<String, String> expectedResponse = Collections.singletonMap("user", "newuser");
                when(authService.createUser(any(com.example.j2n.bff_srv.controller.request.CreateUserRequest.class)))
                                .thenReturn(expectedResponse);

                // Act & Assert
                mockMvc.perform(post(USERS_ENDPOINT)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        void updateUser_ShouldReturnSuccess() throws Exception {
                // Arrange
                String userId = "123";
                com.example.j2n.bff_srv.controller.request.UpdateUserRequest request = new com.example.j2n.bff_srv.controller.request.UpdateUserRequest();
                request.setFullName("Updated User");
                Map<String, String> expectedResponse = Collections.singletonMap("user", "updated");
                when(authService.updateUser(userId, request)).thenReturn(expectedResponse);

                // Act & Assert
                mockMvc.perform(put(USERS_ENDPOINT + "/" + userId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        void deleteUser_ShouldReturnSuccess() throws Exception {
                // Arrange
                String userId = "123";
                Map<String, String> expectedResponse = Collections.singletonMap("message", "deleted");
                when(authService.deleteUser(userId)).thenReturn(expectedResponse);

                // Act & Assert
                mockMvc.perform(delete(USERS_ENDPOINT + "/" + userId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        void getRoles_ShouldReturnSuccess() throws Exception {
                // Arrange
                Map<String, String> expectedResponse = Collections.singletonMap("roles", "[]");
                when(authService.getRoles()).thenReturn(expectedResponse);

                // Act & Assert
                mockMvc.perform(get("/api/bff/roles")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        void getPermissions_ShouldReturnSuccess() throws Exception {
                // Arrange
                Map<String, String> expectedResponse = Collections.singletonMap("permissions", "[]");
                when(authService.getPermissions()).thenReturn(expectedResponse);

                // Act & Assert
                mockMvc.perform(get("/api/bff/permissions")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        void getPermissionsByRoleId_ShouldReturnSuccess() throws Exception {
                // Arrange
                String roleId = "1";
                Map<String, String> expectedResponse = Collections.singletonMap("permissions", "[]");
                when(authService.getPermissionsByRoleId(roleId)).thenReturn(expectedResponse);

                // Act & Assert
                mockMvc.perform(get("/api/bff/roles/" + roleId + "/permissions")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        void logout_ShouldReturnSuccess() throws Exception {
                // Arrange
                Map<String, String> expectedResponse = Collections.singletonMap("message", "logged out");
                when(authService.logout()).thenReturn(expectedResponse);

                // Act & Assert
                mockMvc.perform(post("/api/bff/logout")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }
}
