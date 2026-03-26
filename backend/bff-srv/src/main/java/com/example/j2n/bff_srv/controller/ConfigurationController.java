package com.example.j2n.bff_srv.controller;

import com.example.j2n.bff_srv.controller.request.GetConfigurationsRequest;
import com.example.j2n.bff_srv.controller.request.UpdateConfigurationRequest;
import com.example.j2n.bff_srv.dto.ConfigItemDTO;
import com.example.j2n.bff_srv.service.ConfigurationService;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.enums.BaseMessageEnum;
import com.example.j2n.swagger.annotation.J2NApiExample;
import com.example.j2n.swagger.annotation.J2NApiResponse;
import com.example.j2n.swagger.annotation.J2NApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/bff/configurations")
@RequiredArgsConstructor
public class ConfigurationController {

        private final ConfigurationService configurationService;

        @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Get all configurations", description = "Retrieve all configuration items from config-srv")
        @J2NApiResponses({
                        @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                                        @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
                        })
        })
        public ResponseEntity<BaseResponse<List<ConfigItemDTO>>> getAllConfigurations() {
                return ResponseEntity.ok(configurationService.getAllConfigurations());
        }

        @GetMapping(value = "/{configKey}", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Get configuration by key", description = "Retrieve a specific configuration item by its key")
        @J2NApiResponses({
                        @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                                        @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
                        })
        })
        public ResponseEntity<BaseResponse<ConfigItemDTO>> getConfigurationByKey(@PathVariable String configKey) {
                return ResponseEntity.ok(configurationService.getConfigurationByKey(configKey));
        }

        @PostMapping(value = "/keys", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Get configurations by keys", description = "Retrieve multiple configuration items by a list of keys")
        @J2NApiResponses({
                        @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                                        @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
                        })
        })
        public ResponseEntity<BaseResponse<List<ConfigItemDTO>>> getConfigurationsByKeys(
                        @RequestBody @Valid GetConfigurationsRequest request) {
                return ResponseEntity.ok(configurationService.getConfigurationsByKeys(request));
        }

        @PutMapping(value = "/{configKey}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Update configuration", description = "Update a specific configuration item")
        @J2NApiResponses({
                        @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                                        @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
                        })
        })
        public ResponseEntity<BaseResponse<ConfigItemDTO>> updateConfiguration(@PathVariable String configKey,
                        @RequestBody @Valid UpdateConfigurationRequest request) {
                return ResponseEntity.ok(configurationService.updateConfiguration(configKey, request));
        }
}
