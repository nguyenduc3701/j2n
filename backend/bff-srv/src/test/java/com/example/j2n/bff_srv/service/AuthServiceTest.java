package com.example.j2n.bff_srv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;

import com.example.j2n.bff_srv.constant.GatewayPath;
import com.example.j2n.bff_srv.controller.request.LoginRequest;
import com.example.j2n.bff_srv.controller.request.RegisterRequest;
import com.example.j2n.bff_srv.controller.request.CreateUserRequest;
import com.example.j2n.bff_srv.utils.RestClientUtil;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

        @Mock
        private RestClientUtil restClientUtil;

        @InjectMocks
        private AuthService authService;

        @Test
        void login_ShouldCallRestClientWithCorrectParameters() {
                // Arrange
                LoginRequest request = new LoginRequest();
                request.setUserName("testuser");
                request.setPassword("password");
                Object expectedResponse = new Object();

                when(restClientUtil.request(eq(GatewayPath.AUTH_LOGIN_PATH), eq(HttpMethod.POST), eq(request),
                                eq(Object.class)))
                                .thenReturn(expectedResponse);

                // Act
                Object result = authService.login(request);

                // Assert
                assertEquals(expectedResponse, result);
                verify(restClientUtil).request(eq(GatewayPath.AUTH_LOGIN_PATH), eq(HttpMethod.POST), eq(request),
                                eq(Object.class));
        }

        @Test
        void register_ShouldCallRestClientWithCorrectParameters() {
                // Arrange
                RegisterRequest request = new RegisterRequest();
                request.setUserName("newuser");
                Object expectedResponse = new Object();

                when(restClientUtil.request(eq(GatewayPath.AUTH_REGISTER_PATH), eq(HttpMethod.POST), eq(request),
                                eq(Object.class)))
                                .thenReturn(expectedResponse);

                // Act
                Object result = authService.register(request);

                // Assert
                assertEquals(expectedResponse, result);
                verify(restClientUtil).request(eq(GatewayPath.AUTH_REGISTER_PATH), eq(HttpMethod.POST), eq(request),
                                eq(Object.class));
        }

        @Test
        void getListUsers_ShouldCallRestClientWithCorrectParameters() {
                // Arrange
                com.example.j2n.bff_srv.controller.request.SearchUserRequest request = new com.example.j2n.bff_srv.controller.request.SearchUserRequest();
                Object expectedResponse = new Object();

                when(restClientUtil.request(eq(GatewayPath.AUTH_GET_LIST_USERS_PATH), eq(HttpMethod.POST), eq(request),
                                eq(Object.class)))
                                .thenReturn(expectedResponse);

                // Act
                Object result = authService.getListUsers(request);

                // Assert
                assertEquals(expectedResponse, result);
                verify(restClientUtil).request(eq(GatewayPath.AUTH_GET_LIST_USERS_PATH), eq(HttpMethod.POST),
                                eq(request),
                                eq(Object.class));
        }

        @Test
        void getUserById_ShouldCallRestClientWithCorrectParameters() {
                // Arrange
                String userId = "123";
                Object expectedResponse = new Object();
                String expectedPath = String.format(GatewayPath.AUTH_USER_ID_PATH, userId);

                when(restClientUtil.request(eq(expectedPath), eq(HttpMethod.GET), eq(null), eq(Object.class)))
                                .thenReturn(expectedResponse);

                // Act
                Object result = authService.getUserById(userId);

                // Assert
                assertEquals(expectedResponse, result);
                verify(restClientUtil).request(eq(expectedPath), eq(HttpMethod.GET), eq(null), eq(Object.class));
        }

        @Test
        void getUserMe_ShouldCallRestClientWithCorrectParameters() {
                // Arrange
                Object expectedResponse = new Object();

                when(restClientUtil.request(eq(GatewayPath.AUTH_ME_PATH), eq(HttpMethod.GET), eq(null),
                                eq(Object.class)))
                                .thenReturn(expectedResponse);

                // Act
                Object result = authService.getUserMe();

                // Assert
                assertEquals(expectedResponse, result);
                verify(restClientUtil).request(eq(GatewayPath.AUTH_ME_PATH), eq(HttpMethod.GET), eq(null),
                                eq(Object.class));
        }

        @Test
        void createUser_ShouldCallRestClientWithCorrectParameters() {
                // Arrange
                CreateUserRequest request = new CreateUserRequest();
                request.setUserName("newuser");
                request.setPassword("password123");
                request.setEmail("user@example.com");
                request.setRoleId(2L);
                Object expectedResponse = new Object();

                when(restClientUtil.request(eq(GatewayPath.AUTH_CREATE_USER_PATH), eq(HttpMethod.POST), eq(request),
                                eq(Object.class)))
                                .thenReturn(expectedResponse);

                // Act
                Object result = authService.createUser(request);

                // Assert
                assertEquals(expectedResponse, result);
                verify(restClientUtil).request(eq(GatewayPath.AUTH_CREATE_USER_PATH), eq(HttpMethod.POST), eq(request),
                                eq(Object.class));
        }
}
