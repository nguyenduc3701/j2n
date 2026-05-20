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
import com.example.j2n.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeeService {

    private final FeeRepository feeRepository;

    @LogAround(message = "Get all active fees")
    public List<FeeEntity> getActiveFees() {
        return feeRepository.findAll();
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
    public BaseResponse<FeeEntity> createFee(CreateFeeRequest request) {
        log.info("Creating fee: {}", request.getName());
        FeeEntity fee = FeeEntity.builder()
                .name(request.getName())
                .unitPrice(request.getUnitPrice())
                .unitName(request.getUnitName())
                .isActive(true)
                .build();
        return ResponseFactory.success(feeRepository.save(fee));
    }

    @Transactional
    @LogAround(message = "Update fee configuration")
    public BaseResponse<FeeEntity> updateFee(Long id, UpdateFeeRequest request) {
        FeeEntity config = findByIdOrThrow(id);
        updateEntityFromRequest(config, request);
        return ResponseFactory.success(feeRepository.save(config));
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
