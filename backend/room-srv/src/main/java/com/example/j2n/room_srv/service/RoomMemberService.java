package com.example.j2n.room_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.room_srv.constant.MessageEnum;
import com.example.j2n.room_srv.controller.request.MapMemberToRoomRequest;
import com.example.j2n.room_srv.controller.request.UpdateRoomMemberRequest;
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

    @Transactional
    @LogAround(message = "Map member to room")
    public BaseResponse<List<RoomMemberResponse>> mapMemberToRoom(MapMemberToRoomRequest request) {
        log.info("Mapping user IDs {} to room ID {}, isPrimary: {}", request.getUserIds(), request.getRoomId(),
                request.getIsPrimary());
        RoomEntity room = roomService.findRoomByIdOrThrow(request.getRoomId());
        validateCapacity(room, request);
        validateMappingRequest(request);
        if (Boolean.TRUE.equals(request.getIsPrimary())) {
            demoteExistingPrimaryMembers(request.getRoomId());
        }
        List<RoomMemberEntity> entitiesToSave = request.getUserIds().stream()
                .map(userId -> buildRoomMemberEntity(room, userId, request.getIsPrimary()))
                .collect(Collectors.toList());
        List<RoomMemberEntity> savedEntities = roomMemberRepository.saveAll(entitiesToSave);
        List<RoomMemberResponse> responses = savedEntities.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseFactory.success(responses);
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
    @LogAround(message = "Delete room member")
    public BaseResponse<Void> deleteRoomMember(Long id) {
        log.info("Deleting room member with ID: {}", id);
        RoomMemberEntity entity = findRoomMemberByIdOrThrow(id);
        roomMemberRepository.delete(entity);
        return ResponseFactory.success(null);
    }

    private void validateCapacity(RoomEntity room, MapMemberToRoomRequest request) {
        if (room.getMaxPeople() != null) {
            int currentMemberCount = roomMemberRepository.findByRoomId(room.getId()).size();
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

    private void applyMemberUpdates(RoomMemberEntity entity, UpdateRoomMemberRequest request) {
        request.getIsPrimary().ifPresent(newIsPrimary -> {
            if (newIsPrimary && !Boolean.TRUE.equals(entity.getIsPrimary())) {
                demoteExistingPrimaryMembers(entity.getRoom().getId());
            }
            entity.setIsPrimary(newIsPrimary);
        });
    }

    private void demoteExistingPrimaryMembers(Long roomId) {
        List<RoomMemberEntity> members = roomMemberRepository.findByRoomId(roomId);
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
}
