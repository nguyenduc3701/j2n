package com.example.j2n.auth_srv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.example.j2n.auth_srv.exception.FieldExistedException;
import com.example.j2n.auth_srv.exception.InvalidCredentialException;
import com.example.j2n.auth_srv.utils.JwtGeneralUtil;
import com.example.j2n.utils.RedisUtil;
import io.jsonwebtoken.Claims;
import com.example.j2n.constants.CommonConst;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.InvalidInputException;
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

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private com.example.j2n.auth_srv.messaging.user.publisher.UserEventPublisher userEventPublisher;

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
        when(jwtUtil.generate(any(), anyString(), anyString(), anyLong())).thenReturn("testToken");

        // Act
        BaseResponse<LoginResponse> response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("testToken", response.getData().getAccessToken());
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
        assertThrows(DataNotFoundException.class, () -> authService.login(request));
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
        assertThrows(InvalidCredentialException.class, () -> authService.login(request));
    }

    @Test
    void login_ShouldThrowException_WhenUsernameIsNull() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUserName(null);
        request.setPassword("password");

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> authService.login(request));
    }

    @Test
    void login_ShouldThrowException_WhenUsernameIsEmpty() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUserName("");
        request.setPassword("password");

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> authService.login(request));
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIsNull() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUserName("testuser");
        request.setPassword(null);

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> authService.login(request));
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIsEmpty() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUserName("testuser");
        request.setPassword("");

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> authService.login(request));
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIsTooShort() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUserName("testuser");
        request.setPassword("123"); // Less than 8 chars

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> authService.login(request));
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
    void register_ShouldReturnUser_WhenRequestHasNullOptionalFields() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUserName("newuser");
        request.setPassword("password");
        request.setEmail("test@example.com");
        request.setFullName(null);
        request.setPhoneNumber(null);
        request.setAddress(null);
        request.setCompany(null);

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
        verify(userRepository).save(org.mockito.ArgumentMatchers.argThat(
                user -> user.getFullName() == null &&
                        user.getPhoneNumber() == null &&
                        user.getAddress() == null &&
                        user.getCompany() == null));
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
        assertThrows(FieldExistedException.class, () -> authService.register(request));
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
        assertThrows(FieldExistedException.class, () -> authService.register(request));
    }

    @Test
    void refreshToken_ShouldThrowException_WhenUserNotFoundInDb() {
        // Arrange
        String refreshToken = "validRefreshToken";
        String refreshKey = CommonConst.AUTH_REFRESH_PREFIX + refreshToken;
        Map<String, Object> refreshValue = new HashMap<>();
        refreshValue.put(AuthService.USER_ID_KEY, "1");
        refreshValue.put(AuthService.SESSION_ID_KEY, "session1");

        when(redisUtil.hasKey(refreshKey)).thenReturn(true);
        when(redisUtil.getValue(refreshKey)).thenReturn(refreshValue);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataNotFoundException.class, () -> authService.refreshToken(refreshToken));
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

    @Test
    void logout_ShouldReturnSuccess() {
        // Arrange
        String accessToken = "Bearer testToken";
        String refreshToken = "refreshToken";
        String sessionId = "session1";
        String userId = "1";
        String sessionKey = CommonConst.AUTH_SESSION_PREFIX + sessionId;
        String refreshKey = CommonConst.AUTH_REFRESH_PREFIX + refreshToken;
        String userSessionsKey = CommonConst.AUTH_USER_SESSIONS_PREFIX + userId;

        Claims claims = mock(Claims.class);
        when(jwtUtil.validate("testToken")).thenReturn(claims);
        when(claims.get(AuthService.SESSION_ID_KEY, String.class)).thenReturn(sessionId);
        when(claims.get(AuthService.USER_ID_KEY, String.class)).thenReturn(userId);

        // Act
        BaseResponse<String> response = authService.logout(accessToken, refreshToken);

        // Assert
        assertNotNull(response);
        assertEquals("Logout Success", response.getData());
        verify(redisUtil).deleteKey(sessionKey);
        verify(redisUtil).deleteKey(refreshKey);
        verify(redisUtil).removeSet(userSessionsKey, sessionId);
    }

    @Test
    void logout_ShouldThrowException_WhenAccessTokenIsNull() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> authService.logout(null, "refreshToken"));
    }

    @Test
    void logout_ShouldThrowException_WhenRefreshTokenIsNull() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> authService.logout("Bearer token", null));
    }

    @Test
    void logout_ShouldThrowException_WhenUserIdIsNull() {
        // Arrange
        String accessToken = "Bearer testToken";
        String refreshToken = "refreshToken";
        String sessionId = "session1";

        Claims claims = mock(Claims.class);
        when(jwtUtil.validate("testToken")).thenReturn(claims);
        when(claims.get(AuthService.SESSION_ID_KEY, String.class)).thenReturn(sessionId);
        when(claims.get(AuthService.USER_ID_KEY, String.class)).thenReturn(null);

        // Act & Assert
        assertThrows(com.example.j2n.exception.UnauthorizedException.class,
                () -> authService.logout(accessToken, refreshToken));
    }

    @Test
    void logout_ShouldThrowException_WhenSessionIdIsNull() {
        // Arrange
        String accessToken = "Bearer testToken";
        String refreshToken = "refreshToken";
        String userId = "1";

        Claims claims = mock(Claims.class);
        when(jwtUtil.validate("testToken")).thenReturn(claims);
        when(claims.get(AuthService.SESSION_ID_KEY, String.class)).thenReturn(null);
        when(claims.get(AuthService.USER_ID_KEY, String.class)).thenReturn(userId);

        // Act & Assert
        assertThrows(com.example.j2n.exception.UnauthorizedException.class,
                () -> authService.logout(accessToken, refreshToken));
    }

    @Test
    void refreshToken_ShouldReturnNewToken_WhenValid() {
        // Arrange
        String refreshToken = "validRefreshToken";
        String refreshKey = CommonConst.AUTH_REFRESH_PREFIX + refreshToken;
        String userId = "1";
        String sessionId = "session1";

        Map<String, Object> refreshValue = new HashMap<>();
        refreshValue.put(AuthService.USER_ID_KEY, userId);
        refreshValue.put(AuthService.SESSION_ID_KEY, sessionId);

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRoleId(1L);

        List<String> permissions = Arrays.asList("CAN_VIEW");
        BaseResponse<List<String>> permissionsResponse = new BaseResponse<>();
        permissionsResponse.setData(permissions);

        when(redisUtil.hasKey(refreshKey)).thenReturn(true);
        when(redisUtil.getValue(refreshKey)).thenReturn(refreshValue);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(permissionService.getPermissionsByRoleId("1")).thenReturn(permissionsResponse);
        when(jwtUtil.generate(any(), anyString(), anyString(), anyLong())).thenReturn("newToken");

        // Act
        BaseResponse<LoginResponse> response = authService.refreshToken(refreshToken);

        // Assert
        assertNotNull(response);
        assertEquals("newToken", response.getData().getAccessToken());
        verify(redisUtil).deleteKey(refreshKey);
    }

    @Test
    void refreshToken_ShouldThrowException_WhenRefreshTokenNotFound() {
        // Arrange
        String refreshToken = "unknownToken";
        String refreshKey = CommonConst.AUTH_REFRESH_PREFIX + refreshToken;

        when(redisUtil.hasKey(refreshKey)).thenReturn(false);

        // Act & Assert
        assertThrows(com.example.j2n.auth_srv.exception.InvalidRefreshTokenException.class,
                () -> authService.refreshToken(refreshToken));
    }

    @Test
    void refreshToken_ShouldThrowException_WhenRefreshValueIsNull() {
        // Arrange
        String refreshToken = "validRefreshToken";
        String refreshKey = CommonConst.AUTH_REFRESH_PREFIX + refreshToken;

        when(redisUtil.hasKey(refreshKey)).thenReturn(true);
        when(redisUtil.getValue(refreshKey)).thenReturn(null);

        // Act & Assert
        assertThrows(com.example.j2n.auth_srv.exception.InvalidRefreshTokenException.class,
                () -> authService.refreshToken(refreshToken));
    }

    @Test
    void refreshToken_ShouldThrowException_WhenTokenIsNull() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> authService.refreshToken(null));
    }

    @Test
    void logoutAllDevices_ShouldReturnSuccess() {
        // Arrange
        String userId = "1";
        String userSessionsKey = CommonConst.AUTH_USER_SESSIONS_PREFIX + userId;
        Set<String> sessions = new HashSet<>(Arrays.asList("session1", "session2"));

        when(redisUtil.getSet(userSessionsKey)).thenReturn((Set<Object>) (Set<?>) sessions);

        // Act
        BaseResponse<String> response = authService.logoutAllDevices(userId);

        // Assert
        assertNotNull(response);
        assertEquals("Logout All Devices Success", response.getData());
        verify(redisUtil).deleteKeys(any(List.class));
        verify(redisUtil).deleteKey(userSessionsKey);
    }

    @Test
    void logoutAllDevices_ShouldReturnSuccess_WhenNoSessions() {
        // Arrange
        String userId = "1";
        String userSessionsKey = CommonConst.AUTH_USER_SESSIONS_PREFIX + userId;

        when(redisUtil.getSet(userSessionsKey)).thenReturn(Collections.emptySet());

        // Act
        BaseResponse<String> response = authService.logoutAllDevices(userId);

        // Assert
        assertNotNull(response);
        assertEquals("Logout All Devices Success", response.getData());
        verify(redisUtil).deleteKey(userSessionsKey);
    }

    @Test
    void logoutAllDevices_ShouldReturnSuccess_WhenSessionsIsNull() {
        // Arrange
        String userId = "1";
        String userSessionsKey = CommonConst.AUTH_USER_SESSIONS_PREFIX + userId;

        when(redisUtil.getSet(userSessionsKey)).thenReturn(null);

        // Act
        BaseResponse<String> response = authService.logoutAllDevices(userId);

        // Assert
        assertNotNull(response);
        assertEquals("Logout All Devices Success", response.getData());
        verify(redisUtil).deleteKey(userSessionsKey);
    }

    @Test
    void mapRoleIdToText_ShouldReturnAdmin_WhenRoleIdIsAdmin() {
        // Use reflection to access private method or just call a method that uses it
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setRoleId(CommonConst.ROLE_ADMIN_ID);
        user.setEmail("admin@test.com");
        user.setFullName("Admin User");
        user.setPhoneNumber("123");
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUsername("admin");

        authService.publishUserRegisteredEvent(user);
        verify(userEventPublisher).publishUserRegistered(org.mockito.ArgumentMatchers.argThat(
                event -> "ADMIN".equals(event.getRole())));
    }

    @Test
    void mapRoleIdToText_ShouldReturnRecruiter_WhenRoleIdIsRecruiter() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setRoleId(CommonConst.ROLE_RECRUITER_ID);
        user.setEmail("recruiter@test.com");
        user.setFullName("Recruiter User");
        user.setPhoneNumber("123");
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUsername("recruiter");

        authService.publishUserRegisteredEvent(user);
        verify(userEventPublisher).publishUserRegistered(org.mockito.ArgumentMatchers.argThat(
                event -> "RECRUITER".equals(event.getRole())));
    }

    @Test
    void mapRoleIdToText_ShouldReturnRenter_WhenRoleIdIsRenter() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setRoleId(CommonConst.ROLE_RENTER_ID);
        user.setEmail("renter@test.com");
        user.setFullName("Renter User");
        user.setPhoneNumber("123");
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUsername("renter");

        authService.publishUserRegisteredEvent(user);
        verify(userEventPublisher).publishUserRegistered(org.mockito.ArgumentMatchers.argThat(
                event -> "RENTER".equals(event.getRole())));
    }

    @Test
    void mapRoleIdToText_ShouldReturnVisitor_WhenRoleIdIsUnknown() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setRoleId(999L);
        user.setEmail("visitor@test.com");
        user.setFullName("Visitor User");
        user.setPhoneNumber("123");
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUsername("visitor");

        authService.publishUserRegisteredEvent(user);
        verify(userEventPublisher).publishUserRegistered(org.mockito.ArgumentMatchers.argThat(
                event -> "VISITOR".equals(event.getRole())));
    }

    @Test
    void publishUserRegisteredEvent_ShouldReturnEarly_WhenUserIdIsNull() {
        UserEntity user = new UserEntity();
        user.setId(null);

        authService.publishUserRegisteredEvent(user);
        verify(userEventPublisher, org.mockito.Mockito.never()).publishUserRegistered(any());
    }

    @Test
    void mapRoleIdToText_ShouldReturnVisitor_WhenRoleIdIsNull() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setRoleId(null);
        user.setEmail("visitor@test.com");
        user.setFullName("Visitor User");
        user.setPhoneNumber("123");
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUsername("visitor");

        authService.publishUserRegisteredEvent(user);
        verify(userEventPublisher).publishUserRegistered(org.mockito.ArgumentMatchers.argThat(
                event -> "VISITOR".equals(event.getRole())));
    }
}
