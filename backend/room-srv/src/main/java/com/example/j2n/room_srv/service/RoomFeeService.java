package com.example.j2n.room_srv.service;

import com.example.j2n.room_srv.service.response.RoomFeeResponse;
import com.example.j2n.room_srv.repository.RoomFeeRepository;
import com.example.j2n.room_srv.repository.entity.RoomEntity;
import com.example.j2n.room_srv.repository.entity.RoomFeeEntity;
import com.example.j2n.room_srv.repository.entity.FeeEntity;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.room_srv.constant.MessageEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomFeeService {

    private final RoomFeeRepository roomFeeRepository;
    private final FeeService feeService;

    @Transactional
    public List<RoomFeeResponse> updateRoomFees(RoomEntity room, List<Integer> feeIds) {
        log.info("Updating fees for room id: {}", room.getId());
        roomFeeRepository.deleteByRoomId(room.getId());
        List<RoomFeeEntity> newFees = buildRoomFeeEntities(room, feeIds);
        List<RoomFeeEntity> savedFees = roomFeeRepository.saveAll(newFees);
        return mapToFeeResponseList(savedFees);
    }

    public List<RoomFeeResponse> getRoomFees(Long roomId) {
        log.info("Getting fees for room id: {}", roomId);
        List<RoomFeeEntity> entities = roomFeeRepository.findByRoomId(roomId);
        return mapToFeeResponseList(entities);
    }

    private List<RoomFeeEntity> buildRoomFeeEntities(RoomEntity room, List<Integer> feeIds) {
        log.info("Building room fee entities for room id: {}", room.getId());
        List<Long> longFeeIds = feeIds.stream()
                .filter(Objects::nonNull)
                .map(Integer::longValue)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, FeeEntity> feeMap = feeService.findAllByIds(longFeeIds).stream()
                .collect(Collectors.toMap(FeeEntity::getId, Function.identity()));

        return longFeeIds.stream()
                .map(feeId -> {
                    FeeEntity fee = feeMap.get(feeId);
                    if (fee == null) {
                        throw new DataNotFoundException(
                                MessageEnum.FEE_NOT_FOUND.withArgs(feeId));
                    }

                    RoomFeeEntity roomFee = new RoomFeeEntity();
                    roomFee.setRoom(room);
                    roomFee.setFee(fee);
                    return roomFee;
                })
                .collect(Collectors.toList());
    }

    private List<RoomFeeResponse> mapToFeeResponseList(List<RoomFeeEntity> entities) {
        return entities.stream()
                .map(entity -> mapToFeeResponse(entity))
                .collect(Collectors.toList());
    }

    public RoomFeeResponse mapToFeeResponse(RoomFeeEntity entity) {
        return RoomFeeResponse.builder()
                .id(entity.getId())
                .feeId(entity.getFee().getId())
                .name(entity.getFee().getName())
                .unitPrice(entity.getFee().getUnitPrice())
                .unitName(entity.getFee().getUnitName())
                .build();
    }
}
