package com.example.j2n.auth_srv.service;

import com.example.j2n.auth_srv.constant.MessageEnum;
import com.example.j2n.auth_srv.controllers.requests.CreateUserRequest;
import com.example.j2n.auth_srv.controllers.requests.UpdateUserRequest;
import com.example.j2n.auth_srv.repository.UserRepository;
import com.example.j2n.auth_srv.repository.entity.UserEntity;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

import java.time.LocalDate;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import com.example.j2n.auth_srv.controllers.requests.SearchUsersRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PermissionService permissionService;
    private final AuthService authService;
    private final PasswordUtil passwordUtil;
    private final CurrentUser currentUser;

    public BaseResponse<UserResponse> searchUsers(SearchUsersRequest request) {
        log.info("[AUTH-SRV] Start fetching user list with filter: {}", request);
        validateUnknownFields(request);
        PageRequest pageRequest = PageUtil.buildPageRequest(request.getPage(), request.getSize());
        Specification<UserEntity> spec = buildSpecification(request);
        Page<UserEntity> pageData = userRepository.findAll(spec, pageRequest);
        UserResponse response = new UserResponse();
        response.setUsers(mapUserEntitiesToUserItems(pageData.getContent()));
        response.setPage(PageUtil.buildPagingMeta(pageData));
        log.info("[AUTH-SRV] End fetching user list. Retrieved {} users", pageData.getTotalElements());
        return ResponseFactory.success(response);
    }

    public BaseResponse<UserResponse.UserItem> getMe() {
        log.info("[AUTH-SRV] Start fetching current user details");
        UserEntity userItem = findUserByIdOrThrow(currentUser.getId());
        List<String> permissions = permissionService
                .getPermissionsByRoleId(userItem.getRoleId().toString())
                .getData();
        UserResponse.UserItem response = buildUserItemFromEntity(userItem);
        response.setPermissions(permissions);
        log.info("[AUTH-SRV] End fetching current user details. Current user retrieved: {}", response.getUserName());
        return ResponseFactory.success(response);
    }

    public BaseResponse<UserResponse.UserItem> getUserById(String userId) {
        log.info("[AUTH-SRV] Start fetching user by ID: {}", userId);
        UserResponse.UserItem user = getUserItemById(userId);
        log.info("[AUTH-SRV] End fetching user by ID. User retrieved: {}", user.getUserName());
        return ResponseFactory.success(user);
    }

    public BaseResponse<UserItemResponse> createUser(CreateUserRequest request) {
        log.info("[AUTH-SRV] Start creating user: {}", request.getUserName());
        validateUserRoleCanAction();
        authService.validateUserNameAndPasswordRequest(request.getUserName(), request.getPassword());
        authService.validateUsernameAndEmailDoesNotExist(request.getUserName(), request.getEmail());
        validateAllowRoleInRequest(request.getRoleId());
        UserEntity user = buildUserFromCreateRequest(request);
        userRepository.save(user);
        authService.publishUserRegisteredEvent(user);
        log.info("[AUTH-SRV] End creating user. User created successfully");
        return ResponseFactory.of(MessageEnum.CREATE_USER_SUCCESS, authService.buildUserItemResponse(user));
    }

    public BaseResponse<UserItemResponse> updateUser(String userId, UpdateUserRequest request) {
        log.info("[AUTH-SRV] Start updating user ID: {}", userId);
        validateUserRoleCanAction();
        if (!currentUser.getId().equals(userId)
                && !currentUser.getRoleId().equals(CommonConst.ROLE_ADMIN_ID.toString())) {
            log.error(
                    "[AUTH-SRV] User lacks permission to update another user. Current User ID: {}, Target User ID: {}",
                    currentUser.getId(), userId);
            throw new AccessDeniedException(MessageEnum.ROLE_NOT_ALLOW_ACTION);
        }
        if (request.getRoleId() != null) {
            validateAllowRoleInRequest(request.getRoleId());
        }
        UserEntity user = findUserByIdOrThrow(userId);
        applyUpdateFields(user, request);
        userRepository.save(user);
        log.info("[AUTH-SRV] End updating user. User updated successfully");
        return ResponseFactory.of(MessageEnum.UPDATE_USER_SUCCESS, authService.buildUserItemResponse(user));
    }

    public BaseResponse<Object> deleteUser(String userId) {
        log.info("[AUTH-SRV] Start deleting user ID: {}", userId);
        validateUserRoleCanAction();
        UserEntity user = findUserByIdOrThrow(userId);
        user.setStatus(UserEntity.Status.INACTIVE);
        user.setIsDeleted(true);
        userRepository.save(user);
        log.info("[AUTH-SRV] End deleting user. User deleted successfully");
        return ResponseFactory.of(MessageEnum.DELETE_USER_SUCCESS, null);
    }

    public BaseResponse<UserItemResponse> updateUserImageUrl(String userId, String imageId) {
        log.info("[AUTH-SRV] Start updating user image URL ID: {}", userId);
        UserEntity user = findUserByIdOrThrow(userId);
        String imageFinalUrl = String.format("/api/bff/image/user/%s", imageId);
        user.setImageUrl(imageFinalUrl);
        userRepository.save(user);
        log.info("[AUTH-SRV] End updating user image URL. User updated successfully");
        return ResponseFactory.of(MessageEnum.UPDATE_USER_SUCCESS, authService.buildUserItemResponse(user));
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
        user.setUsername(request.getUserName().trim());
        user.setPassword(passwordUtil.encode(request.getPassword().trim()));
        user.setEmail(request.getEmail().trim());
        user.setFullName(request.getFullName().trim());
        user.setPhoneNumber(request.getPhoneNumber().trim());
        user.setAddress(request.getAddress().trim());
        user.setCompany(request.getCompany().trim());
        user.setRoleId(Objects.requireNonNullElse(request.getRoleId(), CommonConst.ROLE_VISITOR_ID));
        user.setStatus(UserEntity.Status.INACTIVE);
        user.setRoomId(request.getRoomId());
        user.setBirth(request.getBirth());
        return user;
    }

    private void applyUpdateFields(UserEntity user, UpdateUserRequest request) {
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getCompany() != null) {
            user.setCompany(request.getCompany());
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
    }

    // ==================== Mapping Methods ====================

    private List<UserResponse.UserItem> mapUserEntitiesToUserItems(List<UserEntity> users) {
        return users.stream()
                .map(this::buildUserItemFromEntity)
                .toList();
    }

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

    private Specification<UserEntity> buildSpecification(SearchUsersRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("isDeleted"), false));

            if (request.getId().isPresent()) {
                predicates.add(cb.equal(root.get("id"), request.getId().get()));
            }
            if (request.getFullName().isPresent() && !request.getFullName().get().isEmpty()) {
                predicates
                        .add(cb.like(cb.lower(root.get("fullName")),
                                "%" + request.getFullName().get().toLowerCase() + "%"));
            }
            if (request.getEmail().isPresent() && !request.getEmail().get().isEmpty()) {
                predicates
                        .add(cb.like(cb.lower(root.get("email")), "%" + request.getEmail().get().toLowerCase() + "%"));
            }
            if (request.getPhoneNumber().isPresent() && !request.getPhoneNumber().get().isEmpty()) {
                predicates.add(cb.like(root.get("phoneNumber"), "%" + request.getPhoneNumber().get() + "%"));
            }
            if (request.getUserName().isPresent() && !request.getUserName().get().isEmpty()) {
                predicates
                        .add(cb.like(cb.lower(root.get("username")),
                                "%" + request.getUserName().get().toLowerCase() + "%"));
            }
            if (request.getRoleId().isPresent()) {
                predicates.add(cb.equal(root.get("roleId"), request.getRoleId().get()));
            }
            if (request.getStatus().isPresent() && !request.getStatus().get().isEmpty()) {
                try {
                    predicates.add(cb.equal(root.get("status"), UserEntity.Status.valueOf(request.getStatus().get())));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid status filter: {}", request.getStatus().get());
                }
            }
            if (request.getStartDate().isPresent()) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("createdAt").as(LocalDate.class),
                                request.getStartDate().get()));
            }
            if (request.getEndDate().isPresent()) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("createdAt").as(LocalDate.class), request.getEndDate().get()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
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
}
