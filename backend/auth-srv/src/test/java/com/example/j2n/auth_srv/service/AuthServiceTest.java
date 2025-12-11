package com.example.j2n.auth_srv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.example.j2n.auth_srv.utils.JwtGeneralUtil;
import com.example.j2n.constants.CommonConst;
import com.example.j2n.dto.BaseResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.j2n.auth_srv.controllers.requests.ForgotPasswordRequest;
import com.example.j2n.auth_srv.controllers.requests.LoginRequest;
import com.example.j2n.auth_srv.controllers.requests.RegisterRequest;
import com.example.j2n.auth_srv.repository.UserRepository;
import com.example.j2n.auth_srv.repository.entity.UserEntity;
import com.example.j2n.auth_srv.service.response.LoginResponse;
import com.example.j2n.auth_srv.service.response.UserItemResponse;
import com.example.j2n.auth_srv.utils.PasswordUtil;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtGeneralUtil jwtUtil;

    @Mock
    private PasswordUtil passwordUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUserName("testuser");
        request.setPassword("password");

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setRoleId(1L);

        List<String> permissions = Arrays.asList("CAN_VIEW", "CAN_EDIT");
        BaseResponse<List<String>> permissionsResponse = new BaseResponse<>();
        permissionsResponse.setData(permissions);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordUtil.matches("password", "encodedPassword")).thenReturn(true);
        when(permissionService.getPermissionsByRoleId("1")).thenReturn(permissionsResponse);
        when(jwtUtil.generate(any(), anyString())).thenReturn("testToken");

        // Act
        BaseResponse<LoginResponse> response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("testToken", response.getData().getToken());
        verify(permissionService).getPermissionsByRoleId("1");
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUserName("nonexistent");
        request.setPassword("password");

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
    }

    @Test
    void login_ShouldThrowException_WhenPasswordInvalid() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUserName("testuser");
        request.setPassword("wrongpassword");

        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setRoleId(1L);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordUtil.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
    }

    @Test
    void login_ShouldThrowException_WhenUsernameIsNull() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUserName(null);
        request.setPassword("password");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
    }

    @Test
    void login_ShouldThrowException_WhenUsernameIsEmpty() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUserName("");
        request.setPassword("password");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIsNull() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUserName("testuser");
        request.setPassword(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIsEmpty() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUserName("testuser");
        request.setPassword("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIsTooShort() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUserName("testuser");
        request.setPassword("123"); // Less than 8 chars

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
    }

    @Test
    void register_ShouldReturnUser_WhenRequestIsValid() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUserName("newuser");
        request.setPassword("password");
        request.setEmail("test@example.com");
        request.setFullName("Test User");
        request.setPhoneNumber("1234567890");
        request.setAddress("Test Address");
        request.setCompany("Test Company");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordUtil.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity user = invocation.getArgument(0);
            user.setId(1L);
            user.setCreatedAt(LocalDateTime.now());
            return user;
        });

        // Act
        BaseResponse<UserItemResponse> response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("newuser", response.getData().getUserName());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void register_ShouldDefaultToVisitorRole_WhenRoleIsNull() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUserName("visitoruser");
        request.setPassword("password");
        request.setEmail("visitor@example.com");
        request.setFullName("Visitor User");
        request.setPhoneNumber("1234567890");
        request.setAddress("Test Address");
        request.setCompany("Test Company");
        request.setRoleId(null); // Role is null

        when(userRepository.existsByEmail("visitor@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("visitoruser")).thenReturn(false);
        when(passwordUtil.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity user = invocation.getArgument(0);
            user.setId(1L);
            user.setCreatedAt(LocalDateTime.now());
            return user;
        });

        // Act
        BaseResponse<UserItemResponse> response = authService.register(request);

        // Assert
        assertNotNull(response);
        verify(userRepository).save(org.mockito.ArgumentMatchers.argThat(
                user -> user.getRoleId().equals(CommonConst.ROLE_VISITOR_ID)));
    }

    @Test
    void register_ShouldThrowException_WhenUsernameExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUserName("existinguser");
        request.setPassword("password");
        request.setEmail("test@example.com");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
    }

    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUserName("newuser");
        request.setPassword("password");
        request.setEmail("existing@example.com");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
    }

    @Test
    void forgotPassword_ShouldReturnSuccess() {
        // Arrange
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("test@example.com");

        // Act
        BaseResponse<String> response = authService.forgotPassword(request);

        // Assert
        assertNotNull(response);
        assertEquals("Forgot Password Success", response.getData());
    }

    @Test
    void buildUserItemResponse_ShouldMapFieldsCorrectly() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setCreatedAt(LocalDateTime.now());

        // Act
        UserItemResponse response = authService.buildUserItemResponse(user);

        // Assert
        assertNotNull(response);
        assertEquals("1", response.getId());
        assertEquals("testuser", response.getUserName());
        assertEquals("test@example.com", response.getEmail());
        assertNotNull(response.getCreatedAt());
    }
}
