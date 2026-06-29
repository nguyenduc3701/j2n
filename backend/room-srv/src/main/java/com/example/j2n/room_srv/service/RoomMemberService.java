package com.example.j2n.room_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.room_srv.constant.MessageEnum;
import com.example.j2n.room_srv.constant.RoomStatus;
import com.example.j2n.room_srv.controller.request.MapMemberToRoomRequest;
import com.example.j2n.room_srv.controller.request.UpdateRoomMemberRequest;
import com.example.j2n.room_srv.messaging.room.event.RoomMemberMappedEvent;
import com.example.j2n.room_srv.messaging.room.event.RoomMemberRemovedEvent;
import com.example.j2n.room_srv.messaging.room.publisher.RoomEventPublisher;
import com.example.j2n.room_srv.messaging.user.event.UserRegisteredEvent;
import com.example.j2n.room_srv.messaging.user.event.UserUpdatedEvent;
import com.example.j2n.room_srv.messaging.user.event.UserDeletedEvent;
import java.util.Objects;
import com.example.j2n.room_srv.repository.RoomMemberRepository;
import com.example.j2n.room_srv.repository.entity.RoomEntity;
import com.example.j2n.room_srv.repository.entity.RoomMemberEntity;
import com.example.j2n.room_srv.service.response.RoomMemberResponse;
import com.example.j2n.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomMemberService {

    private final RoomMemberRepository roomMemberRepository;
    private final RoomService roomService;
    private final RoomEventPublisher roomEventPublisher;

    @Transactional
    @LogAround(message = "Map member to room")
    public BaseResponse<List<RoomMemberResponse>> mapMemberToRoom(MapMemberToRoomRequest request) {
        log.info("Mapping user IDs {} to room ID {}, isPrimary: {}", request.getUserIds(), request.getRoomId(),
                request.getIsPrimary());
        RoomEntity room = roomService.findRoomByIdOrThrow(request.getRoomId());
        List<RoomMemberEntity> existingMembers = roomMemberRepository.findByRoomId(room.getId());
        validateCapacity(room, existingMembers, request);
        validateMappingRequest(request);
        if (Boolean.TRUE.equals(request.getIsPrimary())) {
            demoteExistingPrimaryMembers(existingMembers);
        }
        
        List<RoomMemberEntity> entitiesToSave = buildRoomMemberEntities(room, request.getUserIds(), existingMembers.isEmpty(), request.getIsPrimary());
        List<RoomMemberEntity> savedEntities = roomMemberRepository.saveAll(entitiesToSave);
        updateRoomStatusBasedOnCapacity(room);
        publishRoomMemberMappedEvent(room.getId(), request.getUserIds());
        List<RoomMemberResponse> responses = savedEntities.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseFactory.success(responses);
    }

    private List<RoomMemberEntity> buildRoomMemberEntities(RoomEntity room, List<Long> userIds, boolean isRoomEmpty, Boolean requestIsPrimary) {
        log.info("Building room member entities for room ID: {}, user IDs: {}, isRoomEmpty: {}, requestIsPrimary: {}", room.getId(), userIds, isRoomEmpty, requestIsPrimary);
        List<RoomMemberEntity> entitiesToSave = new java.util.ArrayList<>();
        for (int i = 0; i < userIds.size(); i++) {
            Long userId = userIds.get(i);
            boolean isPrimary;
            if (isRoomEmpty) {
                isPrimary = (i == 0);
            } else {
                isPrimary = Boolean.TRUE.equals(requestIsPrimary);
            }
            entitiesToSave.add(buildRoomMemberEntity(room, userId, isPrimary));
        }
        return entitiesToSave;
    }

    @Transactional
    @LogAround(message = "Update room member")
    public BaseResponse<RoomMemberResponse> updateRoomMember(Long id, UpdateRoomMemberRequest request) {
        log.info("Updating room member with ID: {}", id);
        RoomMemberEntity entity = findRoomMemberByIdOrThrow(id);
        applyMemberUpdates(entity, request);
        RoomMemberEntity saved = roomMemberRepository.save(entity);
        return ResponseFactory.success(mapToResponse(saved));
    }

    @Transactional
    @LogAround(message = "Delete room member by user id")
    public BaseResponse<Void> deleteRoomMemberByUserId(Long userId) {
        log.info("Deleting room member by user ID: {}", userId);
        RoomMemberEntity entity = findRoomMemberByUserIdOrThrow(userId);
        RoomEntity room = entity.getRoom();
        roomMemberRepository.delete(entity);
        roomMemberRepository.flush();
        updateRoomStatusBasedOnCapacity(room);
        publishRoomMemberRemovedEvent(room, userId);
        return ResponseFactory.success(null);
    }

    @Transactional(readOnly = true)
    @LogAround(message = "Get room members by room id")
    public BaseResponse<List<RoomMemberResponse>> getRoomMembersByRoomId(Long roomId) {
        log.info("Getting room members by room ID: {}", roomId);
        List<RoomMemberResponse> responses = roomMemberRepository.findByRoomId(roomId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseFactory.success(responses);
    }

    private void validateCapacity(RoomEntity room, List<RoomMemberEntity> existingMembers, MapMemberToRoomRequest request) {
        log.info("Validating capacity for room ID: {}", room.getId());
        if (room.getMaxPeople() != null) {
            int currentMemberCount = existingMembers.size();
            int newMembersCount = request.getUserIds().size();
            if (currentMemberCount + newMembersCount > room.getMaxPeople()) {
                log.error(
                        "Room capacity exceeded. Room ID: {}, Max allowed: {}, Current members: {}, New members attempted: {}",
                        room.getId(), room.getMaxPeople(), currentMemberCount, newMembersCount);
                throw new InvalidInputException(MessageEnum.ROOM_MAX_PEOPLE_EXCEEDED.withArgs(room.getMaxPeople()));
            }
        }
    }

    private void validateMappingRequest(MapMemberToRoomRequest request) {
        log.info("Validating mapping request for room ID: {}", request.getRoomId());
        for (Long userId : request.getUserIds()) {
            roomMemberRepository.findByUserId(userId).ifPresent(mapping -> {
                if (mapping.getRoom().getId().equals(request.getRoomId())) {
                    log.error("User with ID {} is already mapped to Room with ID {}", userId,
                            request.getRoomId());
                    throw new InvalidInputException(
                            MessageEnum.MEMBER_ALREADY_MAPPED.withArgs(userId, request.getRoomId()));
                } else {
                    log.error("User with ID {} is already mapped to another Room with ID {}", userId,
                            mapping.getRoom().getId());
                    throw new InvalidInputException(MessageEnum.MEMBER_ALREADY_MAPPED_TO_ANOTHER_ROOM
                            .withArgs(userId, mapping.getRoom().getId()));
                }
            });
        }
    }

    private RoomMemberEntity buildRoomMemberEntity(RoomEntity room, Long userId, Boolean isPrimary) {
        return RoomMemberEntity.builder()
                .room(room)
                .userId(userId)
                .isPrimary(isPrimary)
                .build();
    }

    private RoomMemberEntity findRoomMemberByIdOrThrow(Long id) {
        return roomMemberRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.MEMBER_NOT_FOUND.withArgs(id)));
    }

    private RoomMemberEntity findRoomMemberByUserIdOrThrow(Long userId) {
        return roomMemberRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.MEMBER_NOT_FOUND_BY_USER_ID.withArgs(userId)));
    }

    private void applyMemberUpdates(RoomMemberEntity entity, UpdateRoomMemberRequest request) {
        log.info("Applying member updates for member ID: {}", entity.getId());
        request.getIsPrimary().ifPresent(newIsPrimary -> {
            if (newIsPrimary && !Boolean.TRUE.equals(entity.getIsPrimary())) {
                demoteExistingPrimaryMembers(entity.getRoom().getId());
            }
            entity.setIsPrimary(newIsPrimary);
        });
    }

    private void demoteExistingPrimaryMembers(Long roomId) {
        log.info("Demoting existing primary members for room ID: {}", roomId);
        List<RoomMemberEntity> members = roomMemberRepository.findByRoomId(roomId);
        demoteExistingPrimaryMembers(members);
    }

    private void demoteExistingPrimaryMembers(List<RoomMemberEntity> members) {
        members.forEach(member -> {
            if (Boolean.TRUE.equals(member.getIsPrimary())) {
                member.setIsPrimary(false);
                roomMemberRepository.save(member);
            }
        });
    }

    public RoomMemberResponse mapToResponse(RoomMemberEntity entity) {
        return RoomMemberResponse.builder()
                .id(entity.getId())
                .roomId(entity.getRoom().getId())
                .userId(entity.getUserId())
                .isPrimary(entity.getIsPrimary())
                .joinedAt(entity.getJoinedAt())
                .build();
    }

    @Transactional
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        if ("RENTER".equals(event.getRole()) && event.getRoomId() != null && !event.getRoomId().trim().isEmpty()
                && !"null".equalsIgnoreCase(event.getRoomId().trim())) {
            try {
                Long roomId = Long.parseLong(event.getRoomId().trim());
                Long userId = Long.parseLong(event.getUserId().trim());
                log.info("Handling RENTER registered event for user {} and room {}", userId, roomId);
                if (!roomMemberRepository.existsByRoomIdAndUserId(roomId, userId)) {
                    RoomEntity room = roomService.findRoomByIdOrThrow(roomId);
                    boolean isRoomEmpty = roomMemberRepository.findByRoomId(roomId).isEmpty();
                    RoomMemberEntity newMember = buildRoomMemberEntity(room, userId, isRoomEmpty);
                    roomMemberRepository.save(newMember);
                    log.info("Successfully created RoomMember for user {} in room {}", userId, roomId);
                    updateRoomStatusBasedOnCapacity(room);
                } else {
                    log.info("User {} already mapped to room {}", userId, roomId);
                }
            } catch (NumberFormatException e) {
                log.error("Invalid roomId or userId format in UserRegisteredEvent: {}", event);
            } catch (Exception e) {
                log.error("Error handling UserRegisteredEvent: {}", e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void handleUserUpdatedEvent(UserUpdatedEvent event) {
        log.info("Handling UserUpdatedEvent for user ID: {}", event.getUserId());
        try {
            Long userId = Long.parseLong(event.getUserId());
            
            if (!"RENTER".equals(event.getRole())) {
                deleteRoomMemberByUserIdQuietly(userId);
                return;
            }
            
            Long newRoomId = (event.getRoomId() != null && !event.getRoomId().trim().isEmpty() && !"null".equalsIgnoreCase(event.getRoomId().trim()))
                    ? Long.parseLong(event.getRoomId().trim()) : null;
            Long oldRoomId = (event.getOldRoomId() != null && !event.getOldRoomId().trim().isEmpty() && !"null".equalsIgnoreCase(event.getOldRoomId().trim()))
                    ? Long.parseLong(event.getOldRoomId().trim()) : null;
                    
            if (Objects.equals(newRoomId, oldRoomId)) {
                return;
            }
            
            if (oldRoomId != null) {
                roomMemberRepository.findByUserId(userId).ifPresent(entity -> {
                    if (entity.getRoom().getId().equals(oldRoomId)) {
                        RoomEntity oldRoom = entity.getRoom();
                        roomMemberRepository.delete(entity);
                        roomMemberRepository.flush();
                        updateRoomStatusBasedOnCapacity(oldRoom);
                    }
                });
            }
            
            if (newRoomId != null) {
                if (!roomMemberRepository.existsByRoomIdAndUserId(newRoomId, userId)) {
                    roomMemberRepository.findByUserId(userId).ifPresent(entity -> {
                        RoomEntity oldRoom = entity.getRoom();
                        roomMemberRepository.delete(entity);
                        roomMemberRepository.flush();
                        updateRoomStatusBasedOnCapacity(oldRoom);
                    });
                    
                    RoomEntity room = roomService.findRoomByIdOrThrow(newRoomId);
                    boolean isRoomEmpty = roomMemberRepository.findByRoomId(newRoomId).isEmpty();
                    RoomMemberEntity newMember = buildRoomMemberEntity(room, userId, isRoomEmpty);
                    roomMemberRepository.save(newMember);
                    updateRoomStatusBasedOnCapacity(room);
                    log.info("Successfully moved/added user {} to room {}", userId, newRoomId);
                }
            } else {
                deleteRoomMemberByUserIdQuietly(userId);
            }
        } catch (NumberFormatException e) {
            log.error("Invalid roomId or userId format in UserUpdatedEvent: {}", event);
        } catch (Exception e) {
            log.error("Error handling UserUpdatedEvent: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public void handleUserDeletedEvent(UserDeletedEvent event) {
        try {
            Long userId = Long.parseLong(event.getUserId());
            log.info("Handling UserDeletedEvent for user ID: {}", userId);
            deleteRoomMemberByUserIdQuietly(userId);
        } catch (NumberFormatException e) {
            log.error("Invalid userId format in UserDeletedEvent: {}", event);
        } catch (Exception e) {
            log.error("Error handling UserDeletedEvent: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteRoomMemberByUserIdQuietly(Long userId) {
        log.info("Deleting room member quietly by user ID: {}", userId);
        roomMemberRepository.findByUserId(userId).ifPresent(entity -> {
            RoomEntity room = entity.getRoom();
            roomMemberRepository.delete(entity);
            roomMemberRepository.flush();
            updateRoomStatusBasedOnCapacity(room);
        });
    }

    private void updateRoomStatusBasedOnCapacity(RoomEntity room) {
        if (room.getMaxPeople() != null) {
            int currentMemberCount = roomMemberRepository.findByRoomId(room.getId()).size();
            log.info("Checking room status for room {}: current members = {}, max people = {}, current status = {}",
                    room.getId(), currentMemberCount, room.getMaxPeople(), room.getStatus());
            if (currentMemberCount >= room.getMaxPeople()) {
                if (RoomStatus.AVAILABLE.getValue().equals(room.getStatus())) {
                    roomService.updateRoomStatus(room, RoomStatus.OCCUPIED.getValue());
                }
            } else {
                if (RoomStatus.OCCUPIED.getValue().equals(room.getStatus())) {
                    roomService.updateRoomStatus(room, RoomStatus.AVAILABLE.getValue());
                }
            }
        }
    }

    private void publishRoomMemberRemovedEvent(RoomEntity room, Long userId) {
        roomEventPublisher.publishRoomMemberRemoved(RoomMemberRemovedEvent.builder()
                .roomId(room.getId())
                .userId(userId)
                .roomNumber(room.getRoomNumber())
                .build());
    }

    private void publishRoomMemberMappedEvent(Long roomId, List<Long> userIds) {
        roomEventPublisher.publishRoomMemberMapped(RoomMemberMappedEvent.builder()
                .roomId(roomId)
                .userIds(userIds)
                .build());
    }
}
