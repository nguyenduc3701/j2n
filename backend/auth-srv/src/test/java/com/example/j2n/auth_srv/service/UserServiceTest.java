package com.example.j2n.auth_srv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.example.j2n.auth_srv.controllers.requests.CreateUserRequest;
import com.example.j2n.auth_srv.controllers.requests.UpdateUserRequest;
import com.example.j2n.auth_srv.repository.UserRepository;
import com.example.j2n.auth_srv.repository.entity.UserEntity;
import com.example.j2n.auth_srv.service.response.BaseResponse;
import com.example.j2n.auth_srv.service.response.UserItemResponse;
import com.example.j2n.auth_srv.service.response.UserResponse;
import com.example.j2n.auth_srv.utils.PasswordUtil;
import com.example.j2n.auth_srv.utils.common.CurrentUser;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrentUser currentUser;

    @Mock
    private PermissionService permissionService;

    @Mock
    private AuthService authService;

    @Mock
    private PasswordUtil passwordUtil;

    @InjectMocks
    private UserService userService;

    @Test
    void getUsers_ShouldReturnAllUsers() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRoleId(1L);
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        Page<UserEntity> page = new PageImpl<>(List.of(user));
        when(userRepository.findAllByIsDeletedFalse(any(PageRequest.class))).thenReturn(page);

        // Act
        BaseResponse<UserResponse> response = userService.getUsers();

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getData().getUsers().size());
    }

    @Test
    void getMe_ShouldReturnCurrentUserDetails() {
        // Arrange
        String userId = "1";
        String roleId = "1";
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRoleId(1L);
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        when(currentUser.getCurrentUserId()).thenReturn(userId);
        when(currentUser.getCurrentRoleId()).thenReturn(roleId);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(permissionService.getPermissionsByRoleId(roleId)).thenReturn(BaseResponse.success(List.of("READ")));

        // Act
        BaseResponse<UserResponse.UserItem> response = userService.getMe();

        // Assert
        assertNotNull(response);
        assertEquals("testuser", response.getData().getUserName());
        assertEquals(1, response.getData().getPermissions().size());
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        String userId = "1";
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRoleId(1L);
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        BaseResponse<UserResponse.UserItem> response = userService.getUserById(userId);

        // Assert
        assertNotNull(response);
        assertEquals("testuser", response.getData().getUserName());
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        String userId = "1";
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.getUserById(userId));
    }

    @Test
    void createUser_ShouldThrowException_WhenRoleIsAdmin() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setUserName("adminuser");
        request.setRoleId(1L); // Admin role

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(request));
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        String userId = "1";
        UpdateUserRequest request = new UpdateUserRequest();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, request));
    }

    @Test
    void updateUser_ShouldThrowException_WhenRoleIsAdmin() {
        // Arrange
        String userId = "1";
        UpdateUserRequest request = new UpdateUserRequest();
        request.setRoleId(1L); // Admin role

        UserEntity user = new UserEntity();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, request));
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        String userId = "1";
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(userId));
    }

    @Test
    void mapRoleIdToText_ShouldReturnVisitor_WhenRoleIdIsNull() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setRoleId(null);
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        BaseResponse<UserResponse.UserItem> response = userService.getUserById("1");

        // Assert
        assertEquals("VISITOR", response.getData().getRoleId());
    }

    @Test
    void mapRoleIdToText_ShouldReturnCorrectRole_ForVariousIds() {
        // Test Recruiter (2)
        UserEntity recruiter = new UserEntity();
        recruiter.setId(2L);
        recruiter.setRoleId(2L);
        recruiter.setStatus(UserEntity.Status.ACTIVE);
        recruiter.setCreatedAt(LocalDateTime.now());
        recruiter.setUpdatedAt(LocalDateTime.now());
        when(userRepository.findById(2L)).thenReturn(Optional.of(recruiter));
        assertEquals("RECRUITER", userService.getUserById("2").getData().getRoleId());

        // Test Renter (3)
        UserEntity renter = new UserEntity();
        renter.setId(3L);
        renter.setRoleId(3L);
        renter.setStatus(UserEntity.Status.ACTIVE);
        renter.setCreatedAt(LocalDateTime.now());
        renter.setUpdatedAt(LocalDateTime.now());
        when(userRepository.findById(3L)).thenReturn(Optional.of(renter));
        assertEquals("RENTER", userService.getUserById("3").getData().getRoleId());

        // Test Default (e.g., 99)
        UserEntity other = new UserEntity();
        other.setId(99L);
        other.setRoleId(99L);
        other.setStatus(UserEntity.Status.ACTIVE);
        other.setCreatedAt(LocalDateTime.now());
        other.setUpdatedAt(LocalDateTime.now());
        when(userRepository.findById(99L)).thenReturn(Optional.of(other));
        assertEquals("VISITOR", userService.getUserById("99").getData().getRoleId());
    }

    @Test
    void applyUpdateFields_ShouldUpdateOnlyNonNullFields() {
        // Arrange
        String userId = "1";
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFullName("New Name");
        // Other fields null

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setFullName("Old Name");
        user.setPhoneNumber("Old Phone");
        user.setStatus(UserEntity.Status.ACTIVE);

        UserItemResponse itemResponse = new UserItemResponse();
        itemResponse.setId("1");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(authService.buildUserItemResponse(any(UserEntity.class))).thenReturn(itemResponse);

        // Act
        userService.updateUser(userId, request);

        // Assert
        assertEquals("New Name", user.getFullName());
        assertEquals("Old Phone", user.getPhoneNumber()); // Should remain unchanged
    }

    @Test
    void updateUser_ShouldUpdateAllFields_WhenAllFieldsAreProvided() {
        // Arrange
        String userId = "1";
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFullName("New Name");
        request.setPhoneNumber("New Phone");
        request.setAddress("New Address");
        request.setCompany("New Company");
        request.setBirth(java.time.LocalDate.of(2000, 1, 1));
        request.setStatus(UserEntity.Status.INACTIVE);
        request.setRoleId(2L);

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setFullName("Old Name");
        user.setPhoneNumber("Old Phone");
        user.setAddress("Old Address");
        user.setCompany("Old Company");
        user.setBirth(java.time.LocalDate.of(1990, 1, 1));
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setRoleId(1L);

        UserItemResponse itemResponse = new UserItemResponse();
        itemResponse.setId("1");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(authService.buildUserItemResponse(any(UserEntity.class))).thenReturn(itemResponse);

        // Act
        userService.updateUser(userId, request);

        // Assert
        assertEquals("New Name", user.getFullName());
        assertEquals("New Phone", user.getPhoneNumber());
        assertEquals("New Address", user.getAddress());
        assertEquals("New Company", user.getCompany());
        assertEquals(java.time.LocalDate.of(2000, 1, 1), user.getBirth());
        assertEquals(UserEntity.Status.INACTIVE, user.getStatus());
        assertEquals(2L, user.getRoleId());
    }

    @Test
    void createUser_ShouldReturnUser_WhenRequestIsValid() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setUserName("newuser");
        request.setPassword("password");
        request.setEmail("test@example.com");
        request.setFullName("Test User");
        request.setPhoneNumber("1234567890");
        request.setAddress("Test Address");
        request.setCompany("Test Company");
        request.setRoleId(2L);

        UserEntity savedUser = new UserEntity();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");

        UserItemResponse itemResponse = new UserItemResponse();
        itemResponse.setId("1");
        itemResponse.setUserName("newuser");

        when(passwordUtil.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);
        when(authService.buildUserItemResponse(any(UserEntity.class))).thenReturn(itemResponse);

        // Act
        BaseResponse<UserItemResponse> response = userService.createUser(request);

        // Assert
        assertNotNull(response);
        assertEquals("newuser", response.getData().getUserName());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenRequestIsValid() {
        // Arrange
        String userId = "1";
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFullName("Updated Name");
        request.setRoleId(2L);

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRoleId(1L);

        UserItemResponse itemResponse = new UserItemResponse();
        itemResponse.setId("1");
        itemResponse.setUserName("testuser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);
        when(authService.buildUserItemResponse(any(UserEntity.class))).thenReturn(itemResponse);

        // Act
        BaseResponse<UserItemResponse> response = userService.updateUser(userId, request);

        // Assert
        assertNotNull(response);
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void deleteUser_ShouldReturnSuccess_WhenUserExists() {
        // Arrange
        String userId = "1";
        UserEntity user = new UserEntity();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        BaseResponse<Object> response = userService.deleteUser(userId);

        // Assert
        assertNotNull(response);
        verify(userRepository).deleteById(1L);
    }
}
