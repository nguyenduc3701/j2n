package com.example.j2n.room_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.room_srv.constant.MessageEnum;
import com.example.j2n.room_srv.controller.request.CreateFeeRequest;
import com.example.j2n.room_srv.controller.request.UpdateFeeRequest;
import com.example.j2n.room_srv.repository.FeeRepository;
import com.example.j2n.room_srv.repository.entity.FeeEntity;
import com.example.j2n.room_srv.service.response.FeeResponse;
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
public class FeeService {

    private final FeeRepository feeRepository;

    @LogAround(message = "Get all active fees")
    public List<FeeResponse> getActiveFees() {
        return feeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @LogAround(message = "Find fee by id")
    public FeeEntity findByIdOrThrow(Long id) {
        return feeRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.FEE_NOT_FOUND.withArgs(id)));
    }

    @LogAround(message = "Find all fees with ids")
    public List<FeeEntity> findAllByIds(List<Long> ids) {
        log.info("Finding all fees with ids: {}", ids);
        if (ids == null || ids.isEmpty()) {
            throw new InvalidInputException(BaseMessageEnum.INVALID_REQUEST);
        }
        return feeRepository.findAllById(ids);
    }

    @Transactional
    @LogAround(message = "Create fee")
    public BaseResponse<FeeResponse> createFee(CreateFeeRequest request) {
        log.info("Creating fee: {}", request.getName());
        FeeEntity fee = FeeEntity.builder()
                .name(request.getName())
                .unitPrice(request.getUnitPrice())
                .unitName(request.getUnitName())
                .isActive(true)
                .build();
        return ResponseFactory.success(mapToResponse(feeRepository.save(fee)));
    }

    @Transactional
    @LogAround(message = "Update fee configuration")
    public BaseResponse<FeeResponse> updateFee(Long id, UpdateFeeRequest request) {
        FeeEntity config = findByIdOrThrow(id);
        updateEntityFromRequest(config, request);
        return ResponseFactory.success(mapToResponse(feeRepository.save(config)));
    }

    public FeeResponse mapToResponse(FeeEntity entity) {
        if (entity == null) {
            return null;
        }
        return FeeResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .unitPrice(entity.getUnitPrice())
                .unitName(entity.getUnitName())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private void updateEntityFromRequest(FeeEntity config,
            UpdateFeeRequest request) {
        log.info("Updating fee with id: {}", config.getId());
        request.getName().ifPresent(config::setName);
        request.getUnitPrice().ifPresent(config::setUnitPrice);
        request.getUnitName().ifPresent(config::setUnitName);
        request.getIsActive().ifPresent(config::setIsActive);
    }
}
