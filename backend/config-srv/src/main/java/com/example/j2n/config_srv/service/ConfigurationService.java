package com.example.j2n.config_srv.service;

import com.example.j2n.config_srv.repository.ConfigurationRepository;
import com.example.j2n.config_srv.repository.entity.ConfigurationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;

    public List<ConfigurationEntity> findAll() {
        return configurationRepository.findAll();
    }

    public Optional<ConfigurationEntity> findById(Integer id) {
        return configurationRepository.findById(id);
    }

    public List<ConfigurationEntity> findByServiceIdAndEnvironment(String serviceId, String environment) {
        return configurationRepository.findByServiceIdAndEnvironment(serviceId, environment);
    }

    public Optional<ConfigurationEntity> findByServiceIdAndEnvironmentAndConfigKey(String serviceId, String environment,
            String configKey) {
        return configurationRepository.findByServiceIdAndEnvironmentAndConfigKey(serviceId, environment, configKey);
    }

    public ConfigurationEntity save(ConfigurationEntity configurationEntity) {
        return configurationRepository.save(configurationEntity);
    }

    public Optional<ConfigurationEntity> update(Integer id, ConfigurationEntity configurationEntity) {
        if (configurationRepository.existsById(id)) {
            configurationEntity.setId(id);
            return Optional.of(configurationRepository.save(configurationEntity));
        }
        return Optional.empty();
    }

    public boolean delete(Integer id) {
        if (configurationRepository.existsById(id)) {
            configurationRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
