package com.example.j2n.config_srv.controller;

import com.example.j2n.config_srv.controller.request.GetConfigurationsRequest;
import com.example.j2n.config_srv.controller.request.UpdateConfigurationRequest;
import com.example.j2n.config_srv.service.ConfigurationService;
import com.example.j2n.config_srv.service.response.ConfigItemResponse;
import com.example.j2n.dto.BaseResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/centralize/configurations")
@RequiredArgsConstructor
public class ConfigurationController {

    private final ConfigurationService configurationService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<ConfigItemResponse>>> getAllConfigurations() {
        return ResponseEntity.ok(configurationService.getAllConfigurations());
    }

    @GetMapping("/{configKey}")
    public ResponseEntity<BaseResponse<ConfigItemResponse>> getConfigurationById(@PathVariable String configKey) {
        return ResponseEntity.ok(configurationService.getConfigurationById(configKey));
    }

    @PostMapping("/keys")
    public ResponseEntity<BaseResponse<List<ConfigItemResponse>>> getConfigurationsByKeys(
            @RequestBody GetConfigurationsRequest request) {
        return ResponseEntity.ok(configurationService.getConfigurationsByKeys(request));
    }

    @PutMapping("/{configKey}")
    public ResponseEntity<BaseResponse<ConfigItemResponse>> updateConfiguration(@PathVariable String configKey,
            @RequestBody UpdateConfigurationRequest request) {
        return ResponseEntity.ok(configurationService.updateConfigurationById(configKey, request));
    }
}
