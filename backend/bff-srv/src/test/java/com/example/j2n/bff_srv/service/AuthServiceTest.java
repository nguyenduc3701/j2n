package com.example.j2n.bff_srv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;

import com.example.j2n.bff_srv.client.AuthServiceClient;
import com.example.j2n.bff_srv.constant.GatewayPath;
import com.example.j2n.bff_srv.controller.request.LoginRequest;
import com.example.j2n.bff_srv.controller.request.RegisterRequest;
import com.example.j2n.bff_srv.controller.request.CreateUserRequest;
import com.example.j2n.bff_srv.utils.RestClientUtil;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.bff_srv.service.response.LoginResponse;
import com.example.j2n.bff_srv.service.response.ClientLoginResponse;
import com.example.j2n.utils.ResponseFactory;
import org.springframework.core.ParameterizedTypeReference;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

        @Mock
        private RestClientUtil restClientUtil;

        @Mock
        private AuthServiceClient authServiceClient;

        @InjectMocks
        private AuthService authService;

        @Test
        void login_ShouldCallRestClientWithCorrectParameters() {
                // Arrange
                LoginRequest request = new LoginRequest();
                request.setUserName("testuser");
                request.setPassword("password");

                LoginResponse loginResponse = new LoginResponse();
                loginResponse.setAccessToken("access-token");
                loginResponse.setRefreshToken("refresh-token");
                BaseResponse<LoginResponse> serviceResponse = ResponseFactory.success(loginResponse);

                when(restClientUtil.request(eq(GatewayPath.AUTH_LOGIN_PATH), eq(HttpMethod.POST), eq(request),
                                any(ParameterizedTypeReference.class)))
                                .thenReturn(serviceResponse);

                // Act
                BaseResponse<ClientLoginResponse> result = authService.login(request, null);

                // Assert
                assertNotNull(result);
                assertEquals("access-token", result.getData().getAccessToken());
                verify(restClientUtil).request(eq(GatewayPath.AUTH_LOGIN_PATH), eq(HttpMethod.POST), eq(request),
                                any(ParameterizedTypeReference.class));
                verify(authServiceClient).updateResponseCredentials("access-token", "refresh-token");
        }

        @Test
        void register_ShouldCallRestClientWithCorrectParameters() {
                // Arrange
                RegisterRequest request = new RegisterRequest();
                request.setUserName("newuser");
                Object expectedResponse = new Object();

                when(restClientUtil.request(eq(GatewayPath.AUTH_REGISTER_PATH), eq(HttpMethod.POST), eq(request),
                                any()))
                                .thenReturn(expectedResponse);

                // Act
                Object result = authService.register(request);

                // Assert
                assertEquals(expectedResponse, result);
                verify(restClientUtil).request(eq(GatewayPath.AUTH_REGISTER_PATH), eq(HttpMethod.POST), eq(request),
                                any());
        }

        @Test
        void getListUsers_ShouldCallRestClientWithCorrectParameters() {
                // Arrange
                com.example.j2n.bff_srv.controller.request.SearchUserRequest request = new com.example.j2n.bff_srv.controller.request.SearchUserRequest();
                Object expectedResponse = new Object();

                when(restClientUtil.request(eq(GatewayPath.AUTH_GET_LIST_USERS_PATH), eq(HttpMethod.POST), eq(request),
                                any()))
                                .thenReturn(expectedResponse);

                // Act
                Object result = authService.getListUsers(request);

                // Assert
                assertEquals(expectedResponse, result);
                verify(restClientUtil).request(eq(GatewayPath.AUTH_GET_LIST_USERS_PATH), eq(HttpMethod.POST),
                                eq(request),
                                any());
        }

        @Test
        void getUserById_ShouldCallRestClientWithCorrectParameters() {
                // Arrange
                String userId = "123";
                Object expectedResponse = new Object();
                String expectedPath = String.format(GatewayPath.AUTH_USER_ID_PATH, userId);

                when(restClientUtil.request(eq(expectedPath), eq(HttpMethod.GET), eq(null), any()))
                                .thenReturn(expectedResponse);

                // Act
                Object result = authService.getUserById(userId);

                // Assert
                assertEquals(expectedResponse, result);
                verify(restClientUtil).request(eq(expectedPath), eq(HttpMethod.GET), eq(null), any());
        }

        @Test
        void getUserMe_ShouldCallRestClientWithCorrectParameters() {
                // Arrange
                Object expectedResponse = new Object();

                when(restClientUtil.request(eq(GatewayPath.AUTH_ME_PATH), eq(HttpMethod.GET), eq(null),
                                any()))
                                .thenReturn(expectedResponse);

                // Act
                Object result = authService.getUserMe();

                // Assert
                assertEquals(expectedResponse, result);
                verify(restClientUtil).request(eq(GatewayPath.AUTH_ME_PATH), eq(HttpMethod.GET), eq(null),
                                any());
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
                                any()))
                                .thenReturn(expectedResponse);

                // Act
                Object result = authService.createUser(request);

                // Assert
                assertEquals(expectedResponse, result);
                verify(restClientUtil).request(eq(GatewayPath.AUTH_CREATE_USER_PATH), eq(HttpMethod.POST), eq(request),
                                any());
        }

        @Test
        void updateUser_ShouldCallRestClientWithCorrectParameters() {
                // Arrange
                String userId = "123";
                com.example.j2n.bff_srv.controller.request.UpdateUserRequest request = new com.example.j2n.bff_srv.controller.request.UpdateUserRequest();
                request.setFirstName("Updated");
                request.setLastName("User");
                Object expectedResponse = new Object();
                String expectedPath = String.format(GatewayPath.AUTH_UPDATE_USER_PATH, userId);

                when(restClientUtil.request(eq(expectedPath), eq(HttpMethod.PUT), eq(request), any()))
                                .thenReturn(expectedResponse);

                // Act
                Object result = authService.updateUser(userId, request);

                // Assert
                assertEquals(expectedResponse, result);
                verify(restClientUtil).request(eq(expectedPath), eq(HttpMethod.PUT), eq(request), any());
        }

        @Test
        void deleteUser_ShouldCallRestClientWithCorrectParameters() {
                // Arrange
                String userId = "123";
                Object expectedResponse = new Object();
                String expectedPath = String.format(GatewayPath.AUTH_DELETE_USER_PATH, userId);

                when(restClientUtil.request(eq(expectedPath), eq(HttpMethod.DELETE), eq(null), any()))
                                .thenReturn(expectedResponse);

                // Act
                Object result = authService.deleteUser(userId);

                // Assert
                assertEquals(expectedResponse, result);
                verify(restClientUtil).request(eq(expectedPath), eq(HttpMethod.DELETE), eq(null), any());
        }

        @Test
        void getRoles_ShouldCallRestClientWithCorrectParameters() {
                // Arrange
                Object expectedResponse = new Object();

                when(restClientUtil.request(eq(GatewayPath.AUTH_GET_ROLES_PATH), eq(HttpMethod.GET), eq(null),
                                any()))
                                .thenReturn(expectedResponse);

                // Act
                Object result = authService.getRoles();

                // Assert
                assertEquals(expectedResponse, result);
                verify(restClientUtil).request(eq(GatewayPath.AUTH_GET_ROLES_PATH), eq(HttpMethod.GET), eq(null),
                                any());
        }

        @Test
        void getPermissions_ShouldCallRestClientWithCorrectParameters() {
                // Arrange
                Object expectedResponse = new Object();

                when(restClientUtil.request(eq(GatewayPath.AUTH_GET_PERMISSIONS_PATH), eq(HttpMethod.GET), eq(null),
                                any()))
                                .thenReturn(expectedResponse);

                // Act
                Object result = authService.getPermissions();

                // Assert
                assertEquals(expectedResponse, result);
                verify(restClientUtil).request(eq(GatewayPath.AUTH_GET_PERMISSIONS_PATH), eq(HttpMethod.GET), eq(null),
                                any());
        }

        @Test
        void getPermissionsByRoleId_ShouldCallRestClientWithCorrectParameters() {
                // Arrange
                String roleId = "1";
                Object expectedResponse = new Object();
                String expectedPath = String.format(GatewayPath.AUTH_GET_PERMISSIONS_BY_ROLE_ID_PATH, roleId);

                when(restClientUtil.request(eq(expectedPath), eq(HttpMethod.GET), eq(null), any()))
                                .thenReturn(expectedResponse);

                // Act
                Object result = authService.getPermissionsByRoleId(roleId);

                // Assert
                assertEquals(expectedResponse, result);
                verify(restClientUtil).request(eq(expectedPath), eq(HttpMethod.GET), eq(null), any());
        }

        @Test
        void logout_ShouldCallRestClientWithCorrectParameters() {
                // Arrange
                Object expectedResponse = new Object();
                when(restClientUtil.request(eq(GatewayPath.AUTH_LOGOUT_PATH), eq(HttpMethod.POST), eq(null), any()))
                                .thenReturn(expectedResponse);

                // Act
                Object result = authService.logout();

                // Assert
                assertEquals(expectedResponse, result);
                verify(restClientUtil).request(eq(GatewayPath.AUTH_LOGOUT_PATH), eq(HttpMethod.POST), eq(null), any());
        }

        @Test
        void login_AccessTokenNull_ThrowsUnauthorizedException() {
                // Arrange
                LoginRequest request = new LoginRequest();
                LoginResponse loginResponse = new LoginResponse();
                loginResponse.setAccessToken(null); // Missing token
                BaseResponse<LoginResponse> serviceResponse = ResponseFactory.success(loginResponse);

                when(restClientUtil.request(eq(GatewayPath.AUTH_LOGIN_PATH), eq(HttpMethod.POST), eq(request), any()))
                                .thenReturn(serviceResponse);

                // Act & Assert
                org.junit.jupiter.api.Assertions.assertThrows(com.example.j2n.exception.UnauthorizedException.class,
                                () -> authService.login(request, null));
        }
}
