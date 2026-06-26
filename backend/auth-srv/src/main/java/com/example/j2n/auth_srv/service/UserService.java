package com.example.j2n.auth_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.auth_srv.constant.MessageEnum;
import com.example.j2n.auth_srv.controllers.requests.CreateUserRequest;
import com.example.j2n.auth_srv.controllers.requests.SearchUsersRequest;
import com.example.j2n.auth_srv.controllers.requests.UpdateUserRequest;
import com.example.j2n.auth_srv.repository.UserRepository;
import com.example.j2n.auth_srv.repository.entity.UserEntity;
import com.example.j2n.auth_srv.service.response.DeleteUserReponse;
import com.example.j2n.auth_srv.service.response.UserItemResponse;
import com.example.j2n.auth_srv.service.response.UserResponse;
import com.example.j2n.auth_srv.utils.PasswordUtil;
import com.example.j2n.auth_srv.utils.common.CurrentUser;
import com.example.j2n.constants.CommonConst;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.exception.AccessDeniedException;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.UnknowFieldException;
import com.example.j2n.utils.PageUtil;
import com.example.j2n.utils.ResponseFactory;
import static com.example.j2n.utils.CommonUtils.safeTrim;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.j2n.auth_srv.messaging.user.event.UserUpdatedEvent;
import com.example.j2n.auth_srv.messaging.user.event.UserDeletedEvent;
import com.example.j2n.auth_srv.messaging.user.publisher.UserEventPublisher;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

import com.example.j2n.utils.SearchFactory;
import com.example.j2n.utils.SearchPredicateBuilder.SearchCriteria;
import static com.example.j2n.utils.SearchPredicateBuilder.SearchOperation.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PermissionService permissionService;
    private final AuthService authService;
    private final PasswordUtil passwordUtil;
    private final CurrentUser currentUser;
    private final SearchFactory searchFactory;
    private final UserEventPublisher userEventPublisher;

    @LogAround(message = "Fetching user list")
    public BaseResponse<UserResponse> searchUsers(SearchUsersRequest request) {
        validateUnknownFields(request);
        List<SearchCriteria> criteriaList = buildSearchCriteria(request);
        Page<UserResponse.UserItem> pageData = searchFactory.searchAndMap(
                userRepository,
                criteriaList,
                request,
                this::buildUserItemFromEntity);

        UserResponse response = new UserResponse();
        response.setUsers(pageData.getContent());
        response.setPage(PageUtil.buildPagingMeta(pageData));
        return ResponseFactory.success(response);
    }

    @LogAround(message = "Fetching current user details")
    public BaseResponse<UserResponse.UserItem> getMe() {
        UserEntity userItem = findUserByIdOrThrow(currentUser.getId());
        List<String> permissions = permissionService
                .getPermissionsByRoleId(userItem.getRoleId().toString())
                .getData();
        UserResponse.UserItem response = buildUserItemFromEntity(userItem);
        response.setPermissions(permissions);
        return ResponseFactory.success(response);
    }

    @LogAround(message = "Fetching user by ID")
    public BaseResponse<UserResponse.UserItem> getUserById(String userId) {
        UserResponse.UserItem user = getUserItemById(userId);
        return ResponseFactory.success(user);
    }

    @LogAround(message = "Creating user")
    public BaseResponse<UserItemResponse> createUser(CreateUserRequest request) {
        validateUserRoleCanAction();
        authService.validateUsernameAndEmailDoesNotExist(request.getUserName(), request.getEmail());
        validateAllowRoleInRequest(request.getRoleId());
        UserEntity user = buildUserFromCreateRequest(request);
        userRepository.save(user);
        authService.publishUserRegisteredEvent(user);
        return ResponseFactory.of(MessageEnum.CREATE_USER_SUCCESS, authService.buildUserItemResponse(user));
    }

    @LogAround(message = "Updating user")
    public BaseResponse<UserItemResponse> updateUser(String userId, UpdateUserRequest request) {
        validateUserRoleCanAction();
        if (!currentUser.getId().equals(userId)
                && !currentUser.getRoleId().equals(CommonConst.ROLE_ADMIN_ID.toString())) {
            log.error(
                    "[AUTH-SRV] User lacks permission to update another user. Current User ID: {}, Target User ID: {}",
                    currentUser.getId(), userId);
            throw new AccessDeniedException(MessageEnum.ROLE_NOT_ALLOW_ACTION);
        }
        UserEntity user = findUserByIdOrThrow(userId);
        Long oldRoomId = user.getRoomId();
        String oldRole = mapRoleIdToText(user.getRoleId());
        if (request.getRoleId() != null && !CommonConst.ROLE_ADMIN_ID.equals(user.getRoleId())) {
            validateAllowRoleInRequest(request.getRoleId());
        }
        applyUpdateFields(user, request);
        userRepository.save(user);
        publishUserUpdatedEvent(user, oldRoomId, oldRole);
        return ResponseFactory.of(MessageEnum.UPDATE_USER_SUCCESS, authService.buildUserItemResponse(user));
    }

    @LogAround(message = "Deleting user")
    public BaseResponse<DeleteUserReponse> deleteUser(String userId) {
        validateUserRoleCanAction();
        UserEntity user = findUserByIdOrThrow(userId);
        user.setStatus(UserEntity.Status.INACTIVE);
        user.setIsDeleted(true);
        userRepository.save(user);
        publishUserDeletedEvent(user.getId());
        return ResponseFactory.of(MessageEnum.DELETE_USER_SUCCESS, new DeleteUserReponse(userId));
    }

    @LogAround(message = "Updating user image URL")
    public BaseResponse<UserItemResponse> updateUserImageUrl(String userId, String imageUrl) {
        UserEntity user = findUserByIdOrThrow(userId);
        user.setImageUrl(imageUrl);
        userRepository.save(user);
        return ResponseFactory.of(MessageEnum.UPDATE_USER_SUCCESS, authService.buildUserItemResponse(user));
    }

    @LogAround(message = "Update user room ID")
    public void updateUserRoomId(Long userId, Long roomId) {
        log.info("[AUTH-SRV] Updating room ID for user {} to {}", userId, roomId);
        UserEntity user = findUserByIdOrThrow(userId.toString());
        user.setRoomId(roomId);
        userRepository.save(user);
    }

    private UserEntity findUserByIdOrThrow(String userId) {
        return userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> {
                    log.error("[AUTH-SRV] User not found: {}", userId);
                    return new DataNotFoundException(MessageEnum.USER_NOT_FOUND);
                });
    }

    private UserResponse.UserItem getUserItemById(String userId) {
        UserEntity user = findUserByIdOrThrow(userId);
        return buildUserItemFromEntity(user);
    }

    private UserEntity buildUserFromCreateRequest(CreateUserRequest request) {
        UserEntity user = new UserEntity();
        user.setUsername(safeTrim(request.getUserName()));
        user.setPassword(passwordUtil.encode(safeTrim(request.getPassword())));
        user.setEmail(safeTrim(request.getEmail()));
        user.setFullName(safeTrim(request.getFullName()));
        user.setPhoneNumber(safeTrim(request.getPhoneNumber()));
        user.setAddress(safeTrim(request.getAddress()));
        user.setCompany(safeTrim(request.getCompany()));
        user.setRoleId(Objects.requireNonNullElse(request.getRoleId(), CommonConst.ROLE_VISITOR_ID));
        user.setStatus(UserEntity.Status.INACTIVE);
        user.setRoomId(request.getRoomId());
        user.setBirth(request.getBirth());
        return user;
    }

    private void applyUpdateFields(UserEntity user, UpdateUserRequest request) {
        if (request.getFullName() != null) {
            user.setFullName(safeTrim(request.getFullName()));
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(safeTrim(request.getPhoneNumber()));
        }
        if (request.getAddress() != null) {
            user.setAddress(safeTrim(request.getAddress()));
        }
        if (request.getCompany() != null) {
            user.setCompany(safeTrim(request.getCompany()));
        }
        if (request.getBirth() != null) {
            user.setBirth(request.getBirth());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        if (request.getRoleId() != null) {
            user.setRoleId(request.getRoleId());
        }
        if (request.getImageUrl() != null) {
            user.setImageUrl(request.getImageUrl());
        }
        if (request.getRoomId() != null) {
            user.setRoomId(request.getRoomId());
        }
    }

    // ==================== Mapping Methods ====================

    private UserResponse.UserItem buildUserItemFromEntity(UserEntity user) {
        UserResponse.UserItem userItem = new UserResponse.UserItem();
        userItem.setId(user.getId());
        userItem.setUserName(user.getUsername());
        userItem.setFullName(user.getFullName());
        userItem.setEmail(user.getEmail());
        userItem.setPhoneNumber(user.getPhoneNumber());
        userItem.setBirth(user.getBirth());
        userItem.setImageUrl(user.getImageUrl());
        userItem.setRoomId(Objects.toString(user.getRoomId(), null));
        userItem.setAddress(user.getAddress());
        userItem.setCompany(user.getCompany());
        userItem.setRoleId(mapRoleIdToText(user.getRoleId()));
        userItem.setStatus(user.getStatus().name());
        userItem.setCreatedAt(user.getCreatedAt().toString());
        userItem.setUpdatedAt(user.getUpdatedAt().toString());
        return userItem;
    }

    private String mapRoleIdToText(Long roleId) {
        if (roleId == null) {
            return "VISITOR";
        }
        if (roleId.equals(CommonConst.ROLE_ADMIN_ID)) {
            return "ADMIN";
        }
        if (roleId.equals(CommonConst.ROLE_RECRUITER_ID)) {
            return "RECRUITER";
        }
        if (roleId.equals(CommonConst.ROLE_RENTER_ID)) {
            return "RENTER";
        }
        return "VISITOR";
    }

    private List<SearchCriteria> buildSearchCriteria(SearchUsersRequest request) {
        List<SearchCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(SearchCriteria.builder()
                .fieldName("isDeleted")
                .value(false)
                .operation(EQUAL)
                .build());

        request.getId().ifPresent(id -> criteriaList.add(SearchCriteria.builder()
                .fieldName("id")
                .value(id)
                .operation(EQUAL)
                .build()));
        request.getFullName().ifPresent(name -> criteriaList.add(SearchCriteria.builder()
                .fieldName("fullName")
                .value(name)
                .operation(LIKE)
                .build()));
        request.getEmail().ifPresent(email -> criteriaList.add(SearchCriteria.builder()
                .fieldName("email")
                .value(email)
                .operation(LIKE)
                .build()));
        request.getPhoneNumber().ifPresent(phone -> criteriaList.add(SearchCriteria.builder()
                .fieldName("phoneNumber")
                .value(phone)
                .operation(LIKE)
                .build()));
        request.getUserName().ifPresent(username -> criteriaList.add(SearchCriteria.builder()
                .fieldName("username")
                .value(username)
                .operation(LIKE)
                .build()));
        request.getRoleId().ifPresent(roleId -> criteriaList.add(SearchCriteria.builder()
                .fieldName("roleId")
                .value(roleId)
                .operation(EQUAL)
                .build()));

        request.getStatus().ifPresent(status -> {
            try {
                criteriaList.add(SearchCriteria.builder()
                        .fieldName("status")
                        .value(UserEntity.Status.valueOf(status))
                        .operation(EQUAL)
                        .build());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status filter: {}", status);
            }
        });

        request.getStartDate().ifPresent(start -> criteriaList.add(SearchCriteria.builder()
                .fieldName("createdAt")
                .value(start)
                .operation(GREATER_THAN_EQUAL)
                .build()));
        request.getEndDate().ifPresent(end -> criteriaList.add(SearchCriteria.builder()
                .fieldName("createdAt")
                .value(end)
                .operation(LESS_THAN_EQUAL)
                .build()));
        request.getRoomId().ifPresent(roomId -> criteriaList.add(SearchCriteria.builder()
                .fieldName("roomId")
                .value(roomId)
                .operation(EQUAL)
                .build()));

        return criteriaList;
    }

    // ==================== Validation Methods ====================

    private void validateUserRoleCanAction() {
        String currentRoleId = currentUser.getRoleId();
        if (!currentRoleId.equals(CommonConst.ROLE_ADMIN_ID.toString())
                && !currentRoleId.equals(CommonConst.ROLE_RECRUITER_ID.toString())) {
            log.error("[AUTH-SRV] User lacks permission to create/delete users. Role ID: {}", currentRoleId);
            throw new AccessDeniedException(MessageEnum.ROLE_NOT_ALLOW_ACTION);
        }
    }

    private void validateAllowRoleInRequest(Long roleId) {
        if (CommonConst.ROLE_ADMIN_ID.equals(roleId)) {
            log.error("[AUTH-SRV] Cannot create/update user with ADMIN role. Role ID: {}", roleId);
            throw new AccessDeniedException(MessageEnum.ROLE_NOT_ALLOW_CREATE_USER);
        }
    }

    private void validateUnknownFields(SearchUsersRequest request) {
        if (request.hasUnknownFields()) {
            log.error("[AUTH-SRV] Unknown fields in request: {}", request.getUnknownFields());
            throw new UnknowFieldException(BaseMessageEnum.UNKNOWN_FIELDS);
        }
    }

    private void publishUserUpdatedEvent(UserEntity user, Long oldRoomId, String oldRole) {
        if (user.getId() == null) {
            log.error("[AUTH-SRV] User id is null, cannot publish UserUpdatedEvent");
            return;
        }
        UserUpdatedEvent event = UserUpdatedEvent.builder()
                .userId(user.getId().toString())
                .role(mapRoleIdToText(user.getRoleId()))
                .status(user.getStatus().name())
                .roomId(user.getRoomId() != null ? user.getRoomId().toString() : null)
                .oldRoomId(oldRoomId != null ? oldRoomId.toString() : null)
                .build();
        userEventPublisher.publishUserUpdated(event);
    }

    private void publishUserDeletedEvent(Long userId) {
        UserDeletedEvent event = UserDeletedEvent.builder()
                .userId(userId.toString())
                .build();
        userEventPublisher.publishUserDeleted(event);
    }
}
