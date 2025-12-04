package com.example.j2n.bff_srv.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    @MockBean
    private AuthService authService;

    @MockBean
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
        Map<String, String> expectedResponse = Collections.singletonMap("token", "test-token");

        when(authService.login(any(LoginRequest.class))).thenReturn(expectedResponse);

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
        Map<String, String> expectedResponse = Collections.singletonMap("users", "[]");
        when(authService.getListUsers()).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get(USERS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
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
}
