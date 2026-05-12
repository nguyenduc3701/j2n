package com.example.j2n.room_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.room_srv.constant.MessageEnum;
import com.example.j2n.room_srv.controller.request.RoomRequest;
import com.example.j2n.room_srv.controller.request.RoomFeeDto;
import com.example.j2n.room_srv.controller.request.SearchRoomsRequest;
import com.example.j2n.room_srv.controller.request.UpdateRoomFeeRequest;
import com.example.j2n.room_srv.controller.response.RoomResponse;
import com.example.j2n.room_srv.controller.response.RoomFeeResponse;
import com.example.j2n.room_srv.controller.response.SearchRoomsResponse;
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
import java.util.stream.Collectors;

import static com.example.j2n.utils.SearchPredicateBuilder.SearchOperation.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomFeeService roomFeeService;
    private final SearchFactory searchFactory;

    @LogAround(message = "Get all rooms")
    public List<RoomEntity> getAllRooms() {
        return roomRepository.findAll();
    }

    @LogAround(message = "Search rooms")
    public BaseResponse<SearchRoomsResponse> searchRooms(SearchRoomsRequest request) {
        List<SearchCriteria> criteriaList = buildSearchCriteria(request);
        Page<RoomResponse> pageData = searchFactory.searchAndMap(
                roomRepository,
                criteriaList,
                request,
                this::mapToResponse);

        SearchRoomsResponse response = SearchRoomsResponse.builder()
                .rooms(pageData.getContent())
                .page(PageUtil.buildPagingMeta(pageData))
                .build();
        return ResponseFactory.success(response);
    }

    @LogAround(message = "Get room by ID")
    public BaseResponse<RoomResponse> getRoomById(Long id) {
        RoomEntity room = findRoomByIdOrThrow(id);
        return ResponseFactory.success(mapToResponse(room));
    }

    @Transactional
    @LogAround(message = "Update room")
    public BaseResponse<RoomResponse> updateRoom(Long id, RoomRequest request) {
        RoomEntity room = findRoomByIdOrThrow(id);
        updateEntityFromRequest(room, request);
        RoomEntity updatedRoom = roomRepository.save(room);
        return ResponseFactory.success(mapToResponse(updatedRoom));
    }

    @Transactional
    @LogAround(message = "Update room fees")
    public BaseResponse<List<RoomFeeResponse>> updateRoomFees(Long roomId, UpdateRoomFeeRequest request) {
        RoomEntity room = findRoomByIdOrThrow(roomId);
        List<RoomFeeResponse> response = roomFeeService.updateRoomFees(room, request.getFees());
        return ResponseFactory.success(response);
    }

    public RoomEntity findRoomByIdOrThrow(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.ROOM_NOT_FOUND.withArgs(roomId)));
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
