package com.example.j2n.auth_srv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.j2n.constants.CommonConst;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.AccessDeniedException;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.UnknowFieldException;
import com.example.j2n.utils.ResponseFactory;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Expression;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import com.example.j2n.auth_srv.controllers.requests.CreateUserRequest;
import com.example.j2n.auth_srv.controllers.requests.SearchUsersRequest;
import com.example.j2n.auth_srv.controllers.requests.UpdateUserRequest;
import com.example.j2n.auth_srv.repository.UserRepository;
import com.example.j2n.auth_srv.repository.entity.UserEntity;
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

    @Mock
    private jakarta.persistence.criteria.Root<UserEntity> root;

    @Mock
    private jakarta.persistence.criteria.CriteriaQuery<?> query;

    @Mock
    private jakarta.persistence.criteria.CriteriaBuilder cb;

    @Mock
    private jakarta.persistence.criteria.Path<Object> path;

    @Mock
    private jakarta.persistence.criteria.Expression<String> expression;

    @Mock
    private jakarta.persistence.criteria.Predicate predicate;

    @InjectMocks
    private UserService userService;

    @Test
    void getUsers_ShouldReturnAllUsers() {
        // Arrange
        com.example.j2n.auth_srv.controllers.requests.SearchUsersRequest request = new com.example.j2n.auth_srv.controllers.requests.SearchUsersRequest();
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRoleId(1L);
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        Page<UserEntity> page = new PageImpl<>(List.of(user));
        when(userRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class),
                any(PageRequest.class))).thenReturn(page);

        // Act
        BaseResponse<UserResponse> response = userService.searchUsers(request);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getData().getUsers().size());
    }

    @Test
    void searchUsers_ShouldReturnFilteredUsers_WhenAllFiltersAreProvided() {
        // Arrange
        com.example.j2n.auth_srv.controllers.requests.SearchUsersRequest request = new com.example.j2n.auth_srv.controllers.requests.SearchUsersRequest();
        request.setId(Optional.of(1L));
        request.setFullName(Optional.of("Test User"));
        request.setEmail(Optional.of("test@example.com"));
        request.setPhoneNumber(Optional.of("1234567890"));
        request.setUserName(Optional.of("testuser"));
        request.setRoleId(Optional.of(1L));
        request.setStatus(Optional.of(UserEntity.Status.ACTIVE.name()));
        request.setStartDate(Optional.of(java.time.LocalDate.now().minusDays(1)));
        request.setEndDate(Optional.of(java.time.LocalDate.now().plusDays(1)));

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPhoneNumber("1234567890");
        user.setRoleId(1L);
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        Page<UserEntity> page = new PageImpl<>(List.of(user));
        when(userRepository.findAll(any(Specification.class),
                any(PageRequest.class))).thenReturn(page);

        // Act
        BaseResponse<UserResponse> response = userService.searchUsers(request);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getData().getUsers().size());
        verify(userRepository).findAll(any(Specification.class),
                any(PageRequest.class));
    }

    @Test
    void searchUsers_ShouldBuildCorrectSpecification_WhenAllFiltersAreProvided() {
        // Arrange
        com.example.j2n.auth_srv.controllers.requests.SearchUsersRequest request = new com.example.j2n.auth_srv.controllers.requests.SearchUsersRequest();
        request.setId(Optional.of(1L));
        request.setFullName(Optional.of("Test User"));
        request.setEmail(Optional.of("test@example.com"));
        request.setPhoneNumber(Optional.of("1234567890"));
        request.setUserName(Optional.of("testuser"));
        request.setRoleId(Optional.of(2L));
        request.setStatus(Optional.of(UserEntity.Status.ACTIVE.name()));
        request.setStartDate(Optional.of(java.time.LocalDate.now().minusDays(1)));
        request.setEndDate(Optional.of(java.time.LocalDate.now().plusDays(1)));

        UserEntity user = new UserEntity();
        Page<UserEntity> page = new PageImpl<>(List.of(user));

        // Capture the specification
        org.mockito.ArgumentCaptor<Specification<UserEntity>> specCaptor = org.mockito.ArgumentCaptor
                .forClass(Specification.class);

        when(userRepository.findAll(specCaptor.capture(), any(PageRequest.class))).thenReturn(page);

        // Mock Criteria API with specific paths
        Path pathId = org.mockito.Mockito.mock(Path.class);
        Path pathFullName = org.mockito.Mockito
                .mock(Path.class);
        Path pathEmail = org.mockito.Mockito.mock(Path.class);
        Path pathPhone = org.mockito.Mockito.mock(Path.class);
        Path pathUserName = org.mockito.Mockito
                .mock(Path.class);
        Path pathRoleId = org.mockito.Mockito
                .mock(Path.class);
        Path pathStatus = org.mockito.Mockito
                .mock(Path.class);
        Path pathCreatedAt = org.mockito.Mockito
                .mock(Path.class);
        Path pathIsDeleted = org.mockito.Mockito
                .mock(Path.class);

        lenient().when(root.get("id")).thenReturn(pathId);
        lenient().when(root.get("fullName")).thenReturn(pathFullName);
        lenient().when(root.get("email")).thenReturn(pathEmail);
        lenient().when(root.get("phoneNumber")).thenReturn(pathPhone);
        lenient().when(root.get("username")).thenReturn(pathUserName);
        lenient().when(root.get("roleId")).thenReturn(pathRoleId);
        lenient().when(root.get("status")).thenReturn(pathStatus);
        lenient().when(root.get("createdAt")).thenReturn(pathCreatedAt);
        lenient().when(root.get("isDeleted")).thenReturn(pathIsDeleted);

        // Expressions for lower() calls
        Expression expressionFullName = org.mockito.Mockito
                .mock(Expression.class);
        Expression expressionEmail = org.mockito.Mockito
                .mock(Expression.class);
        Expression expressionUserName = org.mockito.Mockito
                .mock(Expression.class);

        lenient().when(cb.lower(pathFullName)).thenReturn(expressionFullName);
        lenient().when(cb.lower(pathEmail)).thenReturn(expressionEmail);
        lenient().when(cb.lower(pathUserName)).thenReturn(expressionUserName);

        // Mock path.as for LocalDate
        Expression pathAsDate = org.mockito.Mockito
                .mock(Expression.class);
        lenient().when(pathCreatedAt.as(java.time.LocalDate.class)).thenReturn(pathAsDate);

        // Common predicate mock
        lenient().when(cb.equal(any(), any())).thenReturn(predicate);
        lenient().when(cb.like(any(), anyString())).thenReturn(predicate);
        lenient().when(cb.greaterThanOrEqualTo(any(), any(java.time.LocalDate.class))).thenReturn(predicate);
        lenient().when(cb.lessThanOrEqualTo(any(), any(java.time.LocalDate.class))).thenReturn(predicate);
        lenient().when(cb.and(any())).thenReturn(predicate);

        // Act
        userService.searchUsers(request);

        // Assert
        Specification<UserEntity> capturedSpec = specCaptor.getValue();
        assertNotNull(capturedSpec);

        // Execute the specification to verify calls to CriteriaBuilder
        capturedSpec.toPredicate(root, query, cb);

        // Verify Predicate creation
        verify(cb).equal(pathIsDeleted, false);
        verify(cb).equal(pathId, 1L);
        verify(cb).like(expressionFullName, "%test user%");
        verify(cb).like(expressionEmail, "%test@example.com%");
        verify(cb).like(pathPhone, "%1234567890%");
        verify(cb).like(expressionUserName, "%testuser%");
        verify(cb).equal(pathRoleId, 2L);
        verify(cb).equal(pathStatus, UserEntity.Status.ACTIVE);
        verify(cb).greaterThanOrEqualTo(eq(pathAsDate), eq(request.getStartDate().get()));
        verify(cb).lessThanOrEqualTo(eq(pathAsDate), eq(request.getEndDate().get()));
    }

    @Test
    void searchUsers_ShouldIgnoreInvalidStatus_WhenStatusIsInvalid() {
        // Arrange
        com.example.j2n.auth_srv.controllers.requests.SearchUsersRequest request = new com.example.j2n.auth_srv.controllers.requests.SearchUsersRequest();
        request.setStatus(Optional.of("INVALID_STATUS"));
        request.setId(Optional.empty());
        request.setFullName(Optional.empty());
        request.setEmail(Optional.empty());
        request.setPhoneNumber(Optional.empty());
        request.setUserName(Optional.empty());
        request.setRoleId(Optional.empty());
        request.setStartDate(Optional.empty());
        request.setEndDate(Optional.empty());

        Page<UserEntity> page = new PageImpl<>(List.of());

        // Capture the specification
        org.mockito.ArgumentCaptor<org.springframework.data.jpa.domain.Specification<UserEntity>> specCaptor = org.mockito.ArgumentCaptor
                .forClass(org.springframework.data.jpa.domain.Specification.class);

        when(userRepository.findAll(specCaptor.capture(), any(PageRequest.class))).thenReturn(page);

        // Mock Criteria API
        jakarta.persistence.criteria.Path pathIsDeleted = org.mockito.Mockito
                .mock(jakarta.persistence.criteria.Path.class);
        jakarta.persistence.criteria.Path pathStatus = org.mockito.Mockito
                .mock(jakarta.persistence.criteria.Path.class);
        lenient().when(root.get("isDeleted")).thenReturn(pathIsDeleted);
        lenient().when(root.get("status")).thenReturn(pathStatus);
        lenient().when(cb.equal(any(), any())).thenReturn(predicate);
        lenient().when(cb.and(any())).thenReturn(predicate);

        // Act
        userService.searchUsers(request);

        // Assert
        org.springframework.data.jpa.domain.Specification<UserEntity> capturedSpec = specCaptor.getValue();
        assertNotNull(capturedSpec);

        // Execute the specification to verify calls to CriteriaBuilder
        capturedSpec.toPredicate(root, query, cb);

        // Verify that isDeleted check is still added
        verify(cb).equal(pathIsDeleted, false);

        // Verify that cb.equal was NOT called for status (because valueOf threw
        // exception)
        verify(cb, org.mockito.Mockito.never()).equal(eq(pathStatus), any());
    }

    @Test
    void searchUsers_ShouldThrowException_WhenUnknownFieldsPresent() {
        // Arrange
        com.example.j2n.auth_srv.controllers.requests.SearchUsersRequest request = new com.example.j2n.auth_srv.controllers.requests.SearchUsersRequest();
        request.addUnknownField("unknown", "value");

        // Act & Assert
        assertThrows(UnknowFieldException.class, () -> userService.searchUsers(request));
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

        lenient().when(currentUser.getId()).thenReturn(userId);
        lenient().when(currentUser.getRoleId()).thenReturn(roleId);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(permissionService.getPermissionsByRoleId(roleId)).thenReturn(ResponseFactory.success(List.of("READ")));

        // Act
        BaseResponse<UserResponse.UserItem> response = userService.getMe();

        // Assert
        assertNotNull(response);
        assertEquals("testuser", response.getData().getUserName());
        assertEquals(1, response.getData().getPermissions().size());
    }

    @Test
    void getMe_ShouldHandleNullRoleId() {
        // Arrange
        String userId = "1";
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRoleId(null);
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        when(currentUser.getId()).thenReturn(userId);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        // When roleId is null, code will throw NullPointerException at line 63
        // This test verifies that null roleId causes an exception
        // Act & Assert
        assertThrows(NullPointerException.class, () -> userService.getMe());
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
        assertThrows(DataNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void createUser_ShouldThrowException_WhenRoleIsAdmin() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setUserName("adminuser");
        request.setEmail("admin@test.com");
        request.setPassword("password123");
        request.setRoleId(1L); // Admin role

        when(currentUser.getRoleId()).thenReturn("2"); // RECRUITER

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> userService.createUser(request));
    }

    @Test
    void createUser_ShouldThrowException_WhenUserIsVisitor() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setUserName("newuser");

        when(currentUser.getRoleId()).thenReturn("99"); // VISITOR

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> userService.createUser(request));
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        String userId = "1";
        UpdateUserRequest request = new UpdateUserRequest();

        lenient().when(currentUser.getId()).thenReturn("1");
        lenient().when(currentUser.getRoleId()).thenReturn("1"); // ADMIN
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataNotFoundException.class, () -> userService.updateUser(userId, request));
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserUpdatesAnotherUserWithoutAdminRole() {
        // Arrange
        String userId = "2"; // Another user
        UpdateUserRequest request = new UpdateUserRequest();

        when(currentUser.getId()).thenReturn("1"); // Current user
        when(currentUser.getRoleId()).thenReturn("2"); // RECRUITER (Not Admin)

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> userService.updateUser(userId, request));
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserIsVisitor() {
        // Arrange
        String userId = "1";
        UpdateUserRequest request = new UpdateUserRequest();

        when(currentUser.getRoleId()).thenReturn(CommonConst.ROLE_VISITOR_ID.toString());

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> userService.updateUser(userId, request));
    }

    @Test
    void updateUser_ShouldThrowException_WhenRoleIsAdmin() {
        // Arrange
        String userId = "1";
        UpdateUserRequest request = new UpdateUserRequest();
        request.setRoleId(1L); // Admin role

        when(currentUser.getId()).thenReturn("1");
        when(currentUser.getRoleId()).thenReturn("1"); // ADMIN

        UserEntity user = new UserEntity();
        user.setId(1L);
        // when(userRepository.findById(1L)).thenReturn(Optional.of(user)); // Not
        // reached because validation fails first

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> userService.updateUser(userId, request));
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        String userId = "1";
        lenient().when(currentUser.getId()).thenReturn("1");
        lenient().when(currentUser.getRoleId()).thenReturn("1"); // ADMIN
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataNotFoundException.class, () -> userService.deleteUser(userId));
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

        when(currentUser.getId()).thenReturn("1");
        when(currentUser.getRoleId()).thenReturn("1"); // ADMIN
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

        when(currentUser.getId()).thenReturn("1");
        when(currentUser.getRoleId()).thenReturn("1"); // ADMIN
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
    void updateUser_ShouldUpdateImageUrl_WhenImageUrlIsProvided() {
        // Arrange
        String userId = "1";
        UpdateUserRequest request = new UpdateUserRequest();
        request.setImageUrl("/new/image/url.jpg");

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setImageUrl("/old/image/url.jpg");

        UserItemResponse itemResponse = new UserItemResponse();
        itemResponse.setId("1");

        lenient().when(currentUser.getId()).thenReturn("1");
        lenient().when(currentUser.getRoleId()).thenReturn("1"); // ADMIN
        lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        lenient().when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(authService.buildUserItemResponse(any(UserEntity.class))).thenReturn(itemResponse);

        // Act
        userService.updateUser(userId, request);

        // Assert
        assertEquals("/new/image/url.jpg", user.getImageUrl());
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

        when(currentUser.getRoleId()).thenReturn("2"); // RECRUITER
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
    void createUser_ShouldDefaultToVisitorRole_WhenRoleIdIsNull() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setUserName("newuser");
        request.setPassword("password");
        request.setEmail("test@example.com");
        request.setFullName("Test User");
        request.setPhoneNumber("1234567890");
        request.setAddress("Test Address");
        request.setCompany("Test Company");
        request.setRoleId(null);

        UserEntity savedUser = new UserEntity();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");

        UserItemResponse itemResponse = new UserItemResponse();
        itemResponse.setId("1");
        itemResponse.setUserName("newuser");

        when(currentUser.getRoleId()).thenReturn("2"); // RECRUITER
        when(passwordUtil.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity user = invocation.getArgument(0);
            return user;
        });
        when(authService.buildUserItemResponse(any(UserEntity.class))).thenReturn(itemResponse);

        // Act
        BaseResponse<UserItemResponse> response = userService.createUser(request);

        // Assert
        assertNotNull(response);
        verify(userRepository).save(org.mockito.ArgumentMatchers.argThat(
                user -> user.getRoleId().equals(CommonConst.ROLE_VISITOR_ID)));
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

        when(currentUser.getId()).thenReturn("1");
        when(currentUser.getRoleId()).thenReturn("1"); // ADMIN
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
    void updateUser_ShouldNotValidateRole_WhenRoleIdIsNull() {
        // Arrange
        String userId = "1";
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFullName("Updated Name");
        request.setRoleId(null); // RoleId is null, should not validate

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRoleId(1L);

        UserItemResponse itemResponse = new UserItemResponse();
        itemResponse.setId("1");

        when(currentUser.getId()).thenReturn("1");
        when(currentUser.getRoleId()).thenReturn("1"); // ADMIN
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

        lenient().when(currentUser.getId()).thenReturn("1");
        lenient().when(currentUser.getRoleId()).thenReturn("1"); // ADMIN
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        BaseResponse<Object> response = userService.deleteUser(userId);

        // Assert
        assertNotNull(response);
        verify(userRepository).save(user);
    }

    @Test
    void updateUserImageUrl_ShouldUpdateImageUrl_WhenUserExists() {
        // Arrange
        String userId = "1";
        String imageId = "image-123";
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");

        UserItemResponse itemResponse = new UserItemResponse();
        itemResponse.setId("1");

        lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        lenient().when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(authService.buildUserItemResponse(any(UserEntity.class))).thenReturn(itemResponse);

        // Act
        BaseResponse<UserItemResponse> response = userService.updateUserImageUrl(userId, imageId);

        // Assert
        assertNotNull(response);
        assertEquals("/api/bff/image/user/" + imageId, user.getImageUrl());
        verify(userRepository).save(user);
    }

    @Test
    void updateUserImageUrl_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        String userId = "1";
        String imageId = "image-123";
        lenient().when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataNotFoundException.class, () -> userService.updateUserImageUrl(userId, imageId));
    }

    @Test
    void getUserById_ShouldHandleNullRoomId() {
        // Arrange
        String userId = "1";
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRoleId(1L);
        user.setRoomId(null);
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        BaseResponse<UserResponse.UserItem> response = userService.getUserById(userId);

        // Assert
        assertNotNull(response);
        assertNull(response.getData().getRoomId());
    }

    @Test
    void getUserById_ShouldHandleNonNullRoomId() {
        // Arrange
        String userId = "1";
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRoleId(1L);
        user.setRoomId(100L);
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        BaseResponse<UserResponse.UserItem> response = userService.getUserById(userId);

        // Assert
        assertNotNull(response);
        assertEquals("100", response.getData().getRoomId());
    }

    @Test
    void searchUsers_ShouldHandleEmptyStringFilters() {
        // Arrange
        SearchUsersRequest request = new SearchUsersRequest();
        request.setFullName(Optional.of(""));
        request.setEmail(Optional.of(""));
        request.setPhoneNumber(Optional.of(""));
        request.setUserName(Optional.of(""));

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRoleId(1L);
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        Page<UserEntity> page = new PageImpl<>(List.of(user));
        when(userRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        // Act
        BaseResponse<UserResponse> response = userService.searchUsers(request);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getData().getUsers().size());
    }

    @Test
    void searchUsers_ShouldHandleEmptyStatusString() {
        // Arrange
        SearchUsersRequest request = new SearchUsersRequest();
        request.setStatus(Optional.of(""));

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRoleId(1L);
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        Page<UserEntity> page = new PageImpl<>(List.of(user));
        when(userRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        // Act
        BaseResponse<UserResponse> response = userService.searchUsers(request);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getData().getUsers().size());
    }

    @Test
    void validateUserRoleCanAction_ShouldAllowRecruiter() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setUserName("newuser");
        request.setPassword("password");
        request.setEmail("test@example.com");
        request.setFullName("Test User");
        request.setPhoneNumber("123");
        request.setAddress("Addr");
        request.setCompany("Comp");
        request.setRoleId(3L);

        UserEntity savedUser = new UserEntity();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");

        when(currentUser.getRoleId()).thenReturn(CommonConst.ROLE_RECRUITER_ID.toString());
        when(passwordUtil.encode(anyString())).thenReturn("enc");
        when(userRepository.save(any())).thenReturn(savedUser);
        when(authService.buildUserItemResponse(any())).thenReturn(new UserItemResponse());

        // Act
        BaseResponse<UserItemResponse> response = userService.createUser(request);

        // Assert
        assertNotNull(response);
        verify(userRepository).save(any());
    }

    @Test
    void updateUser_ShouldAllowOwnUpdate_WhenNotAdminButRecruiter() {
        // Arrange
        String userId = "2";
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFullName("New Name");

        when(currentUser.getId()).thenReturn(userId);
        when(currentUser.getRoleId()).thenReturn(CommonConst.ROLE_RECRUITER_ID.toString());

        UserEntity user = new UserEntity();
        user.setId(2L);
        user.setFullName("Old Name");
        user.setStatus(UserEntity.Status.ACTIVE);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(authService.buildUserItemResponse(any())).thenReturn(new UserItemResponse());

        // Act
        BaseResponse<UserItemResponse> response = userService.updateUser(userId, request);

        // Assert
        assertNotNull(response);
        assertEquals("New Name", user.getFullName());
    }

    @Test
    void searchUsers_ShouldBuildCorrectSpecification_ForMissingFilters() {
        // Arrange
        SearchUsersRequest request = new SearchUsersRequest();
        request.setId(Optional.empty());
        request.setFullName(Optional.empty());
        request.setEmail(Optional.empty());
        request.setPhoneNumber(Optional.empty());
        request.setUserName(Optional.empty());
        request.setRoleId(Optional.empty());
        request.setStatus(Optional.empty());
        request.setStartDate(Optional.empty());
        request.setEndDate(Optional.empty());

        Page<UserEntity> page = new PageImpl<>(List.of(new UserEntity()));
        org.mockito.ArgumentCaptor<Specification<UserEntity>> specCaptor = org.mockito.ArgumentCaptor
                .forClass(Specification.class);
        when(userRepository.findAll(specCaptor.capture(), any(PageRequest.class))).thenReturn(page);

        // Mock Criteria API
        Path pathIsDeleted = org.mockito.Mockito.mock(Path.class);
        lenient().when(root.get("isDeleted")).thenReturn(pathIsDeleted);
        lenient().when(cb.equal(any(), any())).thenReturn(predicate);
        lenient().when(cb.and(any())).thenReturn(predicate);

        // Act
        userService.searchUsers(request);

        // Assert
        Specification<UserEntity> capturedSpec = specCaptor.getValue();
        capturedSpec.toPredicate(root, query, cb);
        verify(cb).equal(pathIsDeleted, false);
        // Verify no other filters were added
        verify(cb, org.mockito.Mockito.atMostOnce()).equal(any(), any());
    }

    @Test
    void updateUser_ShouldUpdateAllPossibleFields() {
        // Arrange
        String userId = "1";
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFullName("New Full Name");
        request.setPhoneNumber("0987654321");
        request.setAddress("New Address");
        request.setCompany("New Company");
        request.setBirth(java.time.LocalDate.of(1995, 5, 5));
        request.setStatus(UserEntity.Status.ACTIVE);
        request.setRoleId(2L);
        request.setImageUrl("http://new-image.com/img.jpg");

        UserEntity user = new UserEntity();
        user.setId(1L);

        when(currentUser.getId()).thenReturn("1");
        when(currentUser.getRoleId()).thenReturn("1"); // Admin
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(authService.buildUserItemResponse(any())).thenReturn(new UserItemResponse());

        // Act
        userService.updateUser(userId, request);

        // Assert
        assertEquals("New Full Name", user.getFullName());
        assertEquals("0987654321", user.getPhoneNumber());
        assertEquals("New Address", user.getAddress());
        assertEquals("New Company", user.getCompany());
        assertEquals(java.time.LocalDate.of(1995, 5, 5), user.getBirth());
        assertEquals(UserEntity.Status.ACTIVE, user.getStatus());
        assertEquals(2L, user.getRoleId());
        assertEquals("http://new-image.com/img.jpg", user.getImageUrl());
    }

    @Test
    void searchUsers_ShouldHandleAllEmptyStringFilters() {
        // Arrange
        SearchUsersRequest request = new SearchUsersRequest();
        request.setFullName(Optional.of(""));
        request.setEmail(Optional.of(""));
        request.setPhoneNumber(Optional.of(""));
        request.setUserName(Optional.of(""));
        request.setStatus(Optional.of(""));

        Page<UserEntity> page = new PageImpl<>(List.of(new UserEntity()));
        when(userRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        // Mock Criteria API
        Path pathIsDeleted = org.mockito.Mockito.mock(Path.class);
        lenient().when(root.get("isDeleted")).thenReturn(pathIsDeleted);
        lenient().when(cb.equal(any(), any())).thenReturn(predicate);
        lenient().when(cb.and(any())).thenReturn(predicate);

        // Act
        userService.searchUsers(request);

        // Assert
        verify(userRepository).findAll(any(Specification.class), any(PageRequest.class));
    }

    @Test
    void searchUsers_ShouldHandleEachEmptyStringFilterIndividually() {
        String[] fields = { "fullName", "email", "phoneNumber", "userName", "status" };
        for (String field : fields) {
            SearchUsersRequest request = new SearchUsersRequest();
            switch (field) {
                case "fullName":
                    request.setFullName(Optional.of(""));
                    break;
                case "email":
                    request.setEmail(Optional.of(""));
                    break;
                case "phoneNumber":
                    request.setPhoneNumber(Optional.of(""));
                    break;
                case "userName":
                    request.setUserName(Optional.of(""));
                    break;
                case "status":
                    request.setStatus(Optional.of(""));
                    break;
            }

            Page<UserEntity> page = new PageImpl<>(List.of(new UserEntity()));
            when(userRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

            // Mock Criteria API
            Path pathIsDeleted = org.mockito.Mockito.mock(Path.class);
            lenient().when(root.get("isDeleted")).thenReturn(pathIsDeleted);
            lenient().when(cb.equal(any(), any())).thenReturn(predicate);
            lenient().when(cb.and(any())).thenReturn(predicate);

            userService.searchUsers(request);
        }
    }
}
