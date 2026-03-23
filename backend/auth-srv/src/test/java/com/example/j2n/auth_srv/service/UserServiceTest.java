package com.example.j2n.auth_srv.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import com.example.j2n.constants.CommonConst;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.AccessDeniedException;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.UnknowFieldException;
import com.example.j2n.utils.ResponseFactory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.example.j2n.auth_srv.controllers.requests.CreateUserRequest;
import com.example.j2n.auth_srv.controllers.requests.SearchUsersRequest;
import com.example.j2n.auth_srv.controllers.requests.UpdateUserRequest;
import com.example.j2n.auth_srv.repository.UserRepository;
import com.example.j2n.auth_srv.repository.entity.UserEntity;
import com.example.j2n.auth_srv.service.response.UserItemResponse;
import com.example.j2n.auth_srv.service.response.UserResponse;
import com.example.j2n.auth_srv.utils.PasswordUtil;
import com.example.j2n.auth_srv.utils.common.CurrentUser;
import com.example.j2n.utils.SearchFactory;
import com.example.j2n.utils.SearchPredicateBuilder.SearchCriteria;

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
    private SearchFactory searchFactory;

    @InjectMocks
    private UserService userService;

    // ==================== searchUsers Tests ====================

    @Test
    void searchUsers_Success() {
        SearchUsersRequest request = new SearchUsersRequest();
        Page<UserResponse.UserItem> page = new PageImpl<>(List.of(new UserResponse.UserItem()));
        doReturn(page).when(searchFactory).searchAndMap(any(), any(), any(), any());

        BaseResponse<UserResponse> response = userService.searchUsers(request);

        assertNotNull(response);
        assertEquals(1, response.getData().getUsers().size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchUsers_WithAllFilters() {
        SearchUsersRequest request = new SearchUsersRequest();
        request.setId(Optional.of(1L));
        request.setFullName(Optional.of("Full Name"));
        request.setEmail(Optional.of("email@test.com"));
        request.setPhoneNumber(Optional.of("123456"));
        request.setUserName(Optional.of("username"));
        request.setRoleId(Optional.of(2L));
        request.setStatus(Optional.of("ACTIVE"));
        request.setStartDate(Optional.of(java.time.LocalDate.now()));
        request.setEndDate(Optional.of(java.time.LocalDate.now()));
        request.setRoomId(Optional.of(101L));

        Page<UserResponse.UserItem> page = new PageImpl<>(new ArrayList<>());
        ArgumentCaptor<List> criteriaCaptor = ArgumentCaptor.forClass(List.class);
        doReturn(page).when(searchFactory).searchAndMap(any(), criteriaCaptor.capture(), any(), any());

        userService.searchUsers(request);

        List<SearchCriteria> criteriaList = (List<SearchCriteria>) criteriaCaptor.getValue();
        // 1 (default isDeleted) + 10 filters = 11
        assertEquals(11, criteriaList.size());
        
        assertTrue(criteriaList.stream().anyMatch(c -> c.getFieldName().equals("isDeleted") && c.getOptionalValue().orElse(null).equals(false)));
        assertTrue(criteriaList.stream().anyMatch(c -> c.getFieldName().equals("id") && c.getOptionalValue().orElse(null).equals(1L)));
        assertTrue(criteriaList.stream().anyMatch(c -> c.getFieldName().equals("status") && c.getOptionalValue().orElse(null).equals(UserEntity.Status.ACTIVE)));
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchUsers_InvalidStatusIgnored() {
        SearchUsersRequest request = new SearchUsersRequest();
        request.setStatus(Optional.of("INVALID"));

        Page<UserResponse.UserItem> page = new PageImpl<>(new ArrayList<>());
        ArgumentCaptor<List> criteriaCaptor = ArgumentCaptor.forClass(List.class);
        doReturn(page).when(searchFactory).searchAndMap(any(), criteriaCaptor.capture(), any(), any());

        userService.searchUsers(request);

        List<SearchCriteria> criteriaList = (List<SearchCriteria>) criteriaCaptor.getValue();
        assertEquals(1, criteriaList.size()); // Only isDeleted
    }

    @Test
    void searchUsers_UnknownFieldsThrowsException() {
        SearchUsersRequest request = new SearchUsersRequest();
        request.addUnknownField("key", "val");

        assertThrows(UnknowFieldException.class, () -> userService.searchUsers(request));
    }

    // ==================== getMe Tests ====================

    @Test
    void getMe_Success() {
        String userId = "1";
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setRoleId(1L);
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        when(currentUser.getId()).thenReturn(userId);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(permissionService.getPermissionsByRoleId("1")).thenReturn(ResponseFactory.success(List.of("READ")));

        BaseResponse<UserResponse.UserItem> response = userService.getMe();

        assertNotNull(response);
        assertEquals("ADMIN", response.getData().getRoleId());
        assertEquals(1, response.getData().getPermissions().size());
    }

    @Test
    void getMe_UserNotFound() {
        when(currentUser.getId()).thenReturn("1");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> userService.getMe());
    }

    // ==================== getUserById Tests ====================

    @Test
    void getUserById_Success() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setRoleId(2L);
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        BaseResponse<UserResponse.UserItem> response = userService.getUserById("1");

        assertNotNull(response);
        assertEquals("RECRUITER", response.getData().getRoleId());
    }

    // ==================== createUser Tests ====================

    @Test
    void createUser_Success() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUserName("user");
        request.setPassword("pass");
        request.setEmail("e@t.com");
        request.setFullName("Name");
        request.setPhoneNumber("123");
        request.setAddress("Add");
        request.setCompany("Comp");
        request.setRoleId(3L);

        when(currentUser.getRoleId()).thenReturn(CommonConst.ROLE_ADMIN_ID.toString());
        when(passwordUtil.encode(anyString())).thenReturn("enc");
        when(authService.buildUserItemResponse(any())).thenReturn(new UserItemResponse());

        BaseResponse<UserItemResponse> response = userService.createUser(request);

        assertNotNull(response);
        verify(userRepository).save(any(UserEntity.class));
        verify(authService).publishUserRegisteredEvent(any());
    }

    @Test
    void createUser_VisitorLacksPermission() {
        when(currentUser.getRoleId()).thenReturn(CommonConst.ROLE_VISITOR_ID.toString());
        assertThrows(AccessDeniedException.class, () -> userService.createUser(new CreateUserRequest()));
    }

    @Test
    void createUser_ForbiddenAdminRoleInRequest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setRoleId(CommonConst.ROLE_ADMIN_ID);
        when(currentUser.getRoleId()).thenReturn(CommonConst.ROLE_ADMIN_ID.toString());

        assertThrows(AccessDeniedException.class, () -> userService.createUser(request));
    }

    @Test
    void createUser_DefaultRoleVisitor() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUserName("u"); request.setPassword("p"); request.setEmail("e");
        request.setFullName("f"); request.setPhoneNumber("ph"); request.setAddress("a");
        request.setCompany("c");
        request.setRoleId(null);

        when(currentUser.getRoleId()).thenReturn(CommonConst.ROLE_ADMIN_ID.toString());
        when(authService.buildUserItemResponse(any())).thenReturn(new UserItemResponse());

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        userService.createUser(request);

        verify(userRepository).save(captor.capture());
        assertEquals(CommonConst.ROLE_VISITOR_ID, captor.getValue().getRoleId());
    }

    // ==================== updateUser Tests ====================

    @Test
    void updateUser_SuccessOwnUpdate() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFullName("New Name");
        UserEntity user = new UserEntity();
        user.setId(1L);

        when(currentUser.getId()).thenReturn("1");
        when(currentUser.getRoleId()).thenReturn(CommonConst.ROLE_RECRUITER_ID.toString());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(authService.buildUserItemResponse(any())).thenReturn(new UserItemResponse());

        userService.updateUser("1", request);

        assertEquals("New Name", user.getFullName());
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_SuccessAdminUpdateOther() {
        UpdateUserRequest request = new UpdateUserRequest();
        UserEntity user = new UserEntity();
        user.setId(2L);

        when(currentUser.getId()).thenReturn("1");
        when(currentUser.getRoleId()).thenReturn(CommonConst.ROLE_ADMIN_ID.toString());
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(authService.buildUserItemResponse(any())).thenReturn(new UserItemResponse());

        userService.updateUser("2", request);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_ForbiddenNonAdminUpdateOther() {
        when(currentUser.getId()).thenReturn("1");
        when(currentUser.getRoleId()).thenReturn(CommonConst.ROLE_RECRUITER_ID.toString());

        assertThrows(AccessDeniedException.class, () -> userService.updateUser("2", new UpdateUserRequest()));
    }

    @Test
    void updateUser_ForbiddenAssignAdminRole() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setRoleId(CommonConst.ROLE_ADMIN_ID);
        when(currentUser.getId()).thenReturn("1");
        when(currentUser.getRoleId()).thenReturn(CommonConst.ROLE_ADMIN_ID.toString());

        assertThrows(AccessDeniedException.class, () -> userService.updateUser("1", request));
    }

    @Test
    void updateUser_AllFields() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFullName("F"); request.setPhoneNumber("P"); request.setAddress("A");
        request.setCompany("C"); request.setBirth(java.time.LocalDate.now());
        request.setStatus(UserEntity.Status.ACTIVE); request.setRoleId(2L);
        request.setImageUrl("I"); request.setRoomId(10L);

        UserEntity user = new UserEntity();
        user.setId(1L);

        when(currentUser.getId()).thenReturn("1");
        when(currentUser.getRoleId()).thenReturn(CommonConst.ROLE_ADMIN_ID.toString());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(authService.buildUserItemResponse(any())).thenReturn(new UserItemResponse());

        userService.updateUser("1", request);

        assertEquals("F", user.getFullName());
        assertEquals("P", user.getPhoneNumber());
        assertEquals("A", user.getAddress());
        assertEquals("C", user.getCompany());
        assertEquals(UserEntity.Status.ACTIVE, user.getStatus());
        assertEquals(2L, user.getRoleId());
        assertEquals("I", user.getImageUrl());
        assertEquals(10L, user.getRoomId());
    }

    // ==================== deleteUser Tests ====================

    @Test
    void deleteUser_Success() {
        UserEntity user = new UserEntity();
        user.setId(1L);

        when(currentUser.getRoleId()).thenReturn(CommonConst.ROLE_ADMIN_ID.toString());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser("1");

        assertEquals(UserEntity.Status.INACTIVE, user.getStatus());
        assertTrue(user.getIsDeleted());
        verify(userRepository).save(user);
    }

    // ==================== updateUserImageUrl Tests ====================

    @Test
    void updateUserImageUrl_Success() {
        UserEntity user = new UserEntity();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(authService.buildUserItemResponse(any())).thenReturn(new UserItemResponse());

        userService.updateUserImageUrl("1", "img123");

        assertEquals("/api/bff/image/user/img123", user.getImageUrl());
        verify(userRepository).save(user);
    }

    // ==================== Mapping & Role Text Tests ====================

    @Test
    void mapRoleIdToText_AllRoles() {
        Long[] roles = { null, CommonConst.ROLE_ADMIN_ID, CommonConst.ROLE_RECRUITER_ID, CommonConst.ROLE_RENTER_ID, 999L };
        String[] expected = { "VISITOR", "ADMIN", "RECRUITER", "RENTER", "VISITOR" };

        for (int i = 0; i < roles.length; i++) {
            UserEntity user = new UserEntity();
            user.setId(1L);
            user.setRoleId(roles[i]);
            user.setStatus(UserEntity.Status.ACTIVE);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            assertEquals(expected[i], userService.getUserById("1").getData().getRoleId());
        }
    }
    
    @Test
    void buildUserItemFromEntity_NonNullRoomId() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setRoomId(500L);
        user.setStatus(UserEntity.Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertEquals("500", userService.getUserById("1").getData().getRoomId());
    }
}
