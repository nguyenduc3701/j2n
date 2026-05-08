package com.example.j2n.room_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.room_srv.constant.MessageEnum;
import com.example.j2n.room_srv.repository.UtilityConfigRepository;
import com.example.j2n.room_srv.repository.entity.UtilityConfigEntity;
import com.example.j2n.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UtilityConfigService {

    private final UtilityConfigRepository utilityConfigRepository;

    @LogAround(message = "Get all active utility configs")
    public List<UtilityConfigEntity> getActiveConfigs() {
        return utilityConfigRepository.findByIsActiveTrue();
    }

    @Transactional
    @LogAround(message = "Update utility config price")
    public BaseResponse<UtilityConfigEntity> updatePrice(Long id, java.math.BigDecimal newPrice) {
        UtilityConfigEntity config = utilityConfigRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.UTILITY_CONFIG_NOT_FOUND.withArgs(id)));
        config.setUnitPrice(newPrice);
        return ResponseFactory.success(utilityConfigRepository.save(config));
    }
}
