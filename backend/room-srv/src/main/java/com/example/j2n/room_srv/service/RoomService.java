package com.example.j2n.room_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.room_srv.constant.MessageEnum;
import com.example.j2n.room_srv.controller.request.RoomRequest;
import com.example.j2n.room_srv.controller.request.RoomUtilityConfigDto;
import com.example.j2n.room_srv.controller.request.UpdateRoomUtilityRequest;
import com.example.j2n.room_srv.controller.response.RoomResponse;
import com.example.j2n.room_srv.controller.response.RoomUtilityResponse;
import com.example.j2n.room_srv.repository.RoomRepository;
import com.example.j2n.room_srv.repository.RoomUtilityRepository;
import com.example.j2n.room_srv.repository.UtilityConfigRepository;
import com.example.j2n.room_srv.repository.entity.RoomEntity;
import com.example.j2n.room_srv.repository.entity.RoomUtilityEntity;
import com.example.j2n.room_srv.repository.entity.UtilityConfigEntity;
import com.example.j2n.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomUtilityRepository roomUtilityRepository;
    private final UtilityConfigRepository utilityConfigRepository;

    @LogAround(message = "Get all rooms")
    public BaseResponse<List<RoomResponse>> getAllRooms() {
        List<RoomResponse> rooms = roomRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseFactory.success(rooms);
    }

    @LogAround(message = "Get room by ID")
    public BaseResponse<RoomResponse> getRoomById(Long id) {
        RoomEntity room = roomRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.ROOM_NOT_FOUND.withArgs(id)));
        return ResponseFactory.success(mapToResponse(room));
    }

    @Transactional
    @LogAround(message = "Create new room")
    public BaseResponse<RoomResponse> createRoom(RoomRequest request) {
        RoomEntity room = new RoomEntity();
        room.setRoomNumber(request.getRoomNumber());
        room.setFloor(request.getFloor());
        room.setBasePrice(request.getBasePrice());
        room.setArea(request.getArea());
        room.setTotalPeople(request.getTotalPeople());
        room.setStatus(request.getStatus() != null ? request.getStatus() : "AVAILABLE");
        room.setDescription(request.getDescription());
        
        RoomEntity savedRoom = roomRepository.save(room);
        return ResponseFactory.success(mapToResponse(savedRoom));
    }

    @Transactional
    @LogAround(message = "Update room")
    public BaseResponse<RoomResponse> updateRoom(Long id, RoomRequest request) {
        RoomEntity room = roomRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.ROOM_NOT_FOUND.withArgs(id)));
        
        room.setRoomNumber(request.getRoomNumber());
        room.setFloor(request.getFloor());
        room.setBasePrice(request.getBasePrice());
        room.setArea(request.getArea());
        room.setTotalPeople(request.getTotalPeople());
        if (request.getStatus() != null) {
            room.setStatus(request.getStatus());
        }
        room.setDescription(request.getDescription());
        
        RoomEntity updatedRoom = roomRepository.save(room);
        return ResponseFactory.success(mapToResponse(updatedRoom));
    }

    @Transactional
    @LogAround(message = "Delete room")
    public BaseResponse<Void> deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new DataNotFoundException(MessageEnum.ROOM_NOT_FOUND.withArgs(id));
        }
        roomRepository.deleteById(id);
        return ResponseFactory.success(null);
    }

    @Transactional
    @LogAround(message = "Update room utilities")
    public BaseResponse<List<RoomUtilityResponse>> updateRoomUtilities(Long roomId, UpdateRoomUtilityRequest request) {
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.ROOM_NOT_FOUND.withArgs(roomId)));

        // Delete existing utilities
        roomUtilityRepository.deleteByRoomId(roomId);

        List<RoomUtilityEntity> newUtilities = new ArrayList<>();
        for (RoomUtilityConfigDto configDto : request.getUtilityConfigs()) {
            UtilityConfigEntity config = utilityConfigRepository.findById(configDto.getUtilityConfigId())
                    .orElseThrow(() -> new DataNotFoundException(MessageEnum.UTILITY_CONFIG_NOT_FOUND.withArgs(configDto.getUtilityConfigId())));

            RoomUtilityEntity roomUtility = new RoomUtilityEntity();
            roomUtility.setRoom(room);
            roomUtility.setUtilityConfig(config);
            roomUtility.setQuantity(configDto.getQuantity());
            newUtilities.add(roomUtility);
        }

        List<RoomUtilityEntity> savedUtilities = roomUtilityRepository.saveAll(newUtilities);
        
        List<RoomUtilityResponse> response = savedUtilities.stream()
                .map(this::mapToUtilityResponse)
                .collect(Collectors.toList());

        return ResponseFactory.success(response);
    }

    private RoomUtilityResponse mapToUtilityResponse(RoomUtilityEntity entity) {
        return new RoomUtilityResponse(
                entity.getId(),
                entity.getUtilityConfig().getId(),
                entity.getUtilityConfig().getName(),
                entity.getUtilityConfig().getType(),
                entity.getUtilityConfig().getUnitPrice(),
                entity.getUtilityConfig().getUnitName(),
                entity.getQuantity()
        );
    }

    private RoomResponse mapToResponse(RoomEntity entity) {
        return new RoomResponse(
                entity.getId(),
                entity.getRoomNumber(),
                entity.getFloor(),
                entity.getBasePrice(),
                entity.getArea(),
                entity.getTotalPeople(),
                entity.getStatus(),
                entity.getDescription(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
