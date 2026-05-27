package com.example.j2n.room_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.room_srv.constant.MessageEnum;
import com.example.j2n.room_srv.controller.request.RoomRequest;
import com.example.j2n.room_srv.controller.request.CreateRoomRequest;
import com.example.j2n.room_srv.controller.request.SearchRoomsRequest;
import com.example.j2n.room_srv.controller.request.UpdateRoomFeeRequest;
import com.example.j2n.room_srv.controller.request.UpdateRoomAssetRequest;
import com.example.j2n.room_srv.repository.entity.RoomAssetEntity;
import com.example.j2n.room_srv.repository.entity.AssetEntity;
import com.example.j2n.room_srv.service.response.AssetResponse;
import com.example.j2n.room_srv.service.response.RoomResponse;
import com.example.j2n.room_srv.service.response.RoomFeeResponse;
import com.example.j2n.room_srv.service.response.SimpleRoomResponse;
import com.example.j2n.room_srv.utils.RoomSecurityUtil;
import com.example.j2n.room_srv.service.response.SearchRoomsResponse;
import com.example.j2n.room_srv.messaging.room.event.RoomStatusUpdatedEvent;
import com.example.j2n.room_srv.messaging.room.publisher.RoomEventPublisher;
import com.example.j2n.room_srv.repository.RoomRepository;
import com.example.j2n.room_srv.repository.entity.RoomEntity;
import com.example.j2n.utils.PageUtil;
import com.example.j2n.utils.ResponseFactory;
import com.example.j2n.utils.SearchFactory;
import com.example.j2n.utils.SearchPredicateBuilder.SearchCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.j2n.utils.SearchPredicateBuilder.SearchOperation.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomFeeService roomFeeService;
    private final RoomAssetService roomAssetService;
    private final AssetService assetService;
    private final SearchFactory searchFactory;
    private final RoomEventPublisher roomEventPublisher;
    private final RoomSecurityUtil roomSecurityUtil;

    @LogAround(message = "Get all rooms")
    public List<RoomEntity> getAllRooms() {
        return roomRepository.findAll();
    }

    @LogAround(message = "Search rooms")
    public BaseResponse<SearchRoomsResponse> searchRooms(SearchRoomsRequest request) {
        List<SearchCriteria> criteriaList = buildSearchCriteria(request);
        Page<SimpleRoomResponse> pageData = searchFactory.searchAndMap(
                roomRepository,
                criteriaList,
                request,
                this::mapToSimpleResponse);

        SearchRoomsResponse response = SearchRoomsResponse.builder()
                .rooms(pageData.getContent())
                .page(PageUtil.buildPagingMeta(pageData))
                .build();
        return ResponseFactory.success(response);
    }

    @LogAround(message = "Get room by ID")
    public BaseResponse<RoomResponse> getRoomById(Long id) {
        RoomEntity room = findRoomByIdOrThrow(id);
        roomSecurityUtil.checkRoomAccess(room);
        RoomResponse response = mapToResponse(room);
        response.setAssets(getRoomAssets(id));
        response.setFees(roomFeeService.getRoomFees(id));
        return ResponseFactory.success(response);
    }

    private List<AssetResponse> getRoomAssets(Long roomId) {
        return roomAssetService.getAssetsByRoomId(roomId).stream()
                .map(ra -> {
                    AssetResponse response = assetService.mapToResponse(ra.getAsset());
                    response.setQuantity(ra.getQuantity());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    @LogAround(message = "Update room")
    public BaseResponse<RoomResponse> updateRoom(Long id, RoomRequest request) {
        RoomEntity room = findRoomByIdOrThrow(id);
        String oldStatus = room.getStatus();
        updateEntityFromRequest(room, request);
        RoomEntity updatedRoom = roomRepository.save(room);
        publishRoomStatusUpdatedEvent(updatedRoom, oldStatus);
        return ResponseFactory.success(mapToResponse(updatedRoom));
    }

    private void publishRoomStatusUpdatedEvent(RoomEntity room, String oldStatus) {
        if (!Objects.equals(oldStatus, room.getStatus())) {
            roomEventPublisher.publishRoomStatusUpdated(RoomStatusUpdatedEvent.builder()
                    .roomId(room.getId())
                    .oldStatus(oldStatus)
                    .newStatus(room.getStatus())
                    .build());
        }
    }

    @Transactional
    @LogAround(message = "Update room fees")
    public BaseResponse<List<RoomFeeResponse>> updateRoomFees(Long roomId, UpdateRoomFeeRequest request) {
        RoomEntity room = findRoomByIdOrThrow(roomId);
        List<RoomFeeResponse> response = roomFeeService.updateRoomFees(room, request.getFeeIds());
        return ResponseFactory.success(response);
    }

    @Transactional
    @LogAround(message = "Update room assets")
    public BaseResponse<String> updateRoomAssets(Long roomId, UpdateRoomAssetRequest request) {
        RoomEntity room = findRoomByIdOrThrow(roomId);
        List<RoomAssetEntity> assetsToSave = request.getAssets().stream()
                .map(mapping -> {
                    AssetEntity asset = assetService.findByIdOrThrow(mapping.getAssetId());
                    return RoomAssetEntity.builder()
                            .room(room)
                            .asset(asset)
                            .quantity(mapping.getQuantity())
                            .build();
                })
                .collect(Collectors.toList());
        roomAssetService.updateRoomAssets(roomId, assetsToSave);
        return ResponseFactory.success(MessageEnum.MAP_ASSET_TO_ROOM_SUCCESS.getMessage());
    }

    public RoomEntity findRoomByIdOrThrow(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.ROOM_NOT_FOUND.withArgs(roomId)));
    }

    @Transactional
    @LogAround(message = "Create room")
    public BaseResponse<RoomResponse> createRoom(CreateRoomRequest request) {
        RoomEntity room = buildRoomEntityFromRequest(request);
        RoomEntity savedRoom = roomRepository.save(room);
        return ResponseFactory.of(MessageEnum.CREATE_ROOM_SUCCESS, mapToResponse(savedRoom));
    }

    private RoomEntity buildRoomEntityFromRequest(CreateRoomRequest request) {
        return RoomEntity.builder()
                .roomNumber(request.getRoomNumber())
                .floor(request.getFloor())
                .basePrice(request.getBasePrice())
                .area(request.getArea())
                .maxPeople(request.getMaxPeople())
                .status(request.getStatus())
                .currentElectricIndex(request.getCurrentElectricIndex())
                .description(request.getDescription())
                .isDeleted(false)
                .isImmutable(false)
                .build();
    }

    @Transactional
    @LogAround(message = "Delete room (soft delete)")
    public BaseResponse<String> deleteRoom(Long id) {
        RoomEntity room = findRoomByIdOrThrow(id);
        validateRoomIsDeletable(room);
        room.setIsDeleted(true);
        roomRepository.save(room);
        return ResponseFactory.of(MessageEnum.DELETE_ROOM_SUCCESS, null);
    }

    private void validateRoomIsDeletable(RoomEntity room) {
        if (Boolean.TRUE.equals(room.getIsImmutable())) {
            throw new InvalidInputException(MessageEnum.ROOM_IS_IMMUTABLE.withArgs(room.getId()));
        }
    }

    private RoomResponse mapToResponse(RoomEntity entity) {
        return RoomResponse.builder()
                .id(entity.getId())
                .roomNumber(entity.getRoomNumber())
                .floor(entity.getFloor())
                .basePrice(entity.getBasePrice())
                .area(entity.getArea())
                .maxPeople(entity.getMaxPeople())
                .status(entity.getStatus())
                .currentElectricIndex(entity.getCurrentElectricIndex())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .isDeleted(entity.getIsDeleted())
                .isImmutable(entity.getIsImmutable())
                .build();
    }

    private SimpleRoomResponse mapToSimpleResponse(RoomEntity entity) {
        return SimpleRoomResponse.builder()
                .id(entity.getId())
                .roomNumber(entity.getRoomNumber())
                .floor(entity.getFloor())
                .basePrice(entity.getBasePrice())
                .area(entity.getArea())
                .maxPeople(entity.getMaxPeople())
                .status(entity.getStatus())
                .currentElectricIndex(entity.getCurrentElectricIndex())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .isDeleted(entity.getIsDeleted())
                .isImmutable(entity.getIsImmutable())
                .build();
    }

    private List<SearchCriteria> buildSearchCriteria(SearchRoomsRequest request) {
        log.info("Building search criteria for room search request: {}", request);
        List<SearchCriteria> criteriaList = new ArrayList<>();

        request.getRoomNumber().ifPresent(roomNumber -> criteriaList.add(SearchCriteria.builder()
                .fieldName("roomNumber")
                .value(roomNumber)
                .operation(LIKE)
                .build()));

        request.getFloor().ifPresent(floor -> criteriaList.add(SearchCriteria.builder()
                .fieldName("floor")
                .value(floor)
                .operation(EQUAL)
                .build()));

        request.getStatus().ifPresent(status -> criteriaList.add(SearchCriteria.builder()
                .fieldName("status")
                .value(status)
                .operation(EQUAL)
                .build()));

        request.getMinPrice().ifPresent(minPrice -> criteriaList.add(SearchCriteria.builder()
                .fieldName("basePrice")
                .value(minPrice)
                .operation(GREATER_THAN_EQUAL)
                .build()));

        request.getMaxPrice().ifPresent(maxPrice -> criteriaList.add(SearchCriteria.builder()
                .fieldName("basePrice")
                .value(maxPrice)
                .operation(LESS_THAN_EQUAL)
                .build()));

        request.getArea().ifPresent(area -> criteriaList.add(SearchCriteria.builder()
                .fieldName("area")
                .value(area)
                .operation(LIKE)
                .build()));

        request.getMaxPeople().ifPresent(maxPeople -> criteriaList.add(SearchCriteria.builder()
                .fieldName("maxPeople")
                .value(maxPeople)
                .operation(EQUAL)
                .build()));

        return criteriaList;
    }

    private void updateEntityFromRequest(RoomEntity room, RoomRequest request) {
        log.info("Updating room entity with id: {}", room.getId());
        request.getRoomNumber().ifPresent(room::setRoomNumber);
        request.getFloor().ifPresent(room::setFloor);
        request.getBasePrice().ifPresent(room::setBasePrice);
        request.getArea().ifPresent(room::setArea);
        request.getMaxPeople().ifPresent(room::setMaxPeople);
        request.getStatus().ifPresent(room::setStatus);
        request.getCurrentElectricIndex().ifPresent(room::setCurrentElectricIndex);
        request.getDescription().ifPresent(room::setDescription);
    }
}
