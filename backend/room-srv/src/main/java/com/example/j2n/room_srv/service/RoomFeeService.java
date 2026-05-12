package com.example.j2n.room_srv.service;

import com.example.j2n.room_srv.controller.request.RoomFeeDto;
import com.example.j2n.room_srv.controller.response.RoomFeeResponse;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomFeeService {

    private final RoomFeeRepository roomFeeRepository;
    private final FeeService feeService;

    @Transactional
    public List<RoomFeeResponse> updateRoomFees(RoomEntity room, List<RoomFeeDto> configDtos) {
        log.info("Updating fees for room id: {}", room.getId());
        roomFeeRepository.deleteByRoomId(room.getId());
        List<RoomFeeEntity> newFees = buildRoomFeeEntities(room, configDtos);
        List<RoomFeeEntity> savedFees = roomFeeRepository.saveAll(newFees);
        return mapToFeeResponseList(savedFees);
    }

    private List<RoomFeeEntity> buildRoomFeeEntities(RoomEntity room, List<RoomFeeDto> configDtos) {
        log.info("Building room fee entities for room id: {}", room.getId());
        List<Long> feeIds = configDtos.stream()
                .map(RoomFeeDto::getFeeId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, FeeEntity> feeMap = feeService.findAllByIds(feeIds).stream()
                .collect(Collectors.toMap(FeeEntity::getId, Function.identity()));

        return configDtos.stream()
                .map(configDto -> mapToRoomFeeEntity(room, configDto, feeMap))
                .collect(Collectors.toList());
    }

    private RoomFeeEntity mapToRoomFeeEntity(RoomEntity room, RoomFeeDto configDto,
            Map<Long, FeeEntity> feeMap) {
        log.info("Mapping room fee entity for room id: {}", room.getId());
        FeeEntity fee = feeMap.get(configDto.getFeeId());
        if (fee == null) {
            throw new DataNotFoundException(
                    MessageEnum.FEE_NOT_FOUND.withArgs(configDto.getFeeId()));
        }

        RoomFeeEntity roomFee = new RoomFeeEntity();
        roomFee.setRoom(room);
        roomFee.setFee(fee);
        return roomFee;
    }

    private List<RoomFeeResponse> mapToFeeResponseList(List<RoomFeeEntity> entities) {
        return entities.stream()
                .map(entity -> mapToFeeResponse(entity))
                .collect(Collectors.toList());
    }

    private RoomFeeResponse mapToFeeResponse(RoomFeeEntity entity) {
        return RoomFeeResponse.builder()
                .id(entity.getId())
                .feeId(entity.getFee().getId())
                .name(entity.getFee().getName())
                .unitPrice(entity.getFee().getUnitPrice())
                .unitName(entity.getFee().getUnitName())
                .build();
    }
}
