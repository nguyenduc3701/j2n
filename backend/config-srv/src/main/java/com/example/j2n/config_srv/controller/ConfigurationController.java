package com.example.j2n.config_srv.controller;

import com.example.j2n.config_srv.repository.entity.ConfigurationEntity;
import com.example.j2n.config_srv.service.ConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/configurations")
@RequiredArgsConstructor
public class ConfigurationController {

    private final ConfigurationService configurationService;

    @GetMapping
    public ResponseEntity<List<ConfigurationEntity>> getAllConfigurations() {
        return ResponseEntity.ok(configurationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConfigurationEntity> getConfigurationById(@PathVariable Integer id) {
        return configurationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{serviceId}/{environment}")
    public ResponseEntity<List<ConfigurationEntity>> getConfigurationsByServiceAndEnvironment(
            @PathVariable String serviceId,
            @PathVariable String environment) {
        List<ConfigurationEntity> configs = configurationService.findByServiceIdAndEnvironment(serviceId, environment);
        if (configs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(configs);
    }

    @GetMapping("/{serviceId}/{environment}/{configKey}")
    public ResponseEntity<ConfigurationEntity> getConfigurationByKey(
            @PathVariable String serviceId,
            @PathVariable String environment,
            @PathVariable String configKey) {
        return configurationService.findByServiceIdAndEnvironmentAndConfigKey(serviceId, environment, configKey)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ConfigurationEntity> createConfiguration(
            @RequestBody ConfigurationEntity configurationEntity) {
        return ResponseEntity.ok(configurationService.save(configurationEntity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConfigurationEntity> updateConfiguration(@PathVariable Integer id,
            @RequestBody ConfigurationEntity configurationEntity) {
        return configurationService.update(id, configurationEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConfiguration(@PathVariable Integer id) {
        if (configurationService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
