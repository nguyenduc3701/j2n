package com.example.j2n.config_srv.service;

import com.example.j2n.config_srv.constant.ConfigConstants;
import com.example.j2n.config_srv.constant.MessageEnum;
import com.example.j2n.config_srv.controller.request.UpdateConfigurationRequest;
import com.example.j2n.config_srv.repository.ConfigurationRepository;
import com.example.j2n.config_srv.repository.entity.ConfigurationEntity;
import com.example.j2n.config_srv.service.response.ConfigItemResponse;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.utils.ResponseFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;

    public BaseResponse<List<ConfigItemResponse>> getAllConfigurations() {
        log.info("[CONFIG-SRV] Start fetching all configurations");
        List<ConfigurationEntity> response = configurationRepository.findAll();
        List<ConfigItemResponse> configItemResponses = response.stream().map(this::buildConfigItemResponse).toList();
        log.info("[CONFIG-SRV] End fetching all configurations. Retrieved {} configurations",
                configItemResponses.size());
        return ResponseFactory.success(configItemResponses);
    }

    public BaseResponse<ConfigItemResponse> getConfigurationById(String configKey) {
        log.info("[CONFIG-SRV] Start fetching configuration by ID: {}", configKey);
        ConfigurationEntity response = findConfigByKeyOrThrow(configKey);
        ConfigItemResponse configItemResponse = buildConfigItemResponse(response);
        log.info("[CONFIG-SRV] End fetching configuration by ID. Retrieved configuration: {}", response.getConfigKey());
        return ResponseFactory.success(configItemResponse);
    }

    public BaseResponse<ConfigItemResponse> updateConfigurationById(String configKey,
            UpdateConfigurationRequest request) {
        log.info("[CONFIG-SRV] Start updating configuration by ID: {}", configKey);
        validateUpdateConfigurationRequest(request);
        ConfigurationEntity config = findConfigByKeyOrThrow(configKey);
        if (ConfigConstants.isUnupdatable(config.getConfigKey())) {
            throw new InvalidInputException(MessageEnum.CONFIGURATION_NOT_UPDATABLE);
        }
        config.setConfigValue(request.getValue());
        ConfigurationEntity response = configurationRepository.save(config);
        ConfigItemResponse configItemResponse = buildConfigItemResponse(response);
        log.info("[CONFIG-SRV] End updating configuration by ID. Updated configuration: {}", response.getConfigKey());
        return ResponseFactory.success(configItemResponse);
    }

    private ConfigurationEntity findConfigByKeyOrThrow(String configKey) {
        if (configKey == null || configKey.isBlank()) {
            log.error("[CONFIG-SRV] Invalid request: configKey is null or blank");
            throw new InvalidInputException(MessageEnum.INVALID_REQUEST);
        }
        return configurationRepository.findByConfigKey(configKey)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.CONFIGURATION_NOT_FOUND));
    }

    private void validateUpdateConfigurationRequest(UpdateConfigurationRequest request) {
        if (request == null || request.getValue() == null) {
            log.error("[CONFIG-SRV] Invalid request: request is null");
            throw new InvalidInputException(MessageEnum.INVALID_REQUEST);
        }
    }

    private ConfigItemResponse buildConfigItemResponse(ConfigurationEntity config) {
        ConfigItemResponse response = new ConfigItemResponse();
        response.setConfigKey(config.getConfigKey());
        response.setConfigValue(config.getConfigValue());
        return response;
    }
}
