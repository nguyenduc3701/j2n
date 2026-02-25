package com.example.j2n.config_srv.repository;

import com.example.j2n.config_srv.repository.entity.ConfigurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigurationRepository extends JpaRepository<ConfigurationEntity, Integer> {
    List<ConfigurationEntity> findByServiceIdAndEnvironment(String serviceId, String environment);
    Optional<ConfigurationEntity> findByServiceIdAndEnvironmentAndConfigKey(String serviceId, String environment, String configKey);
}
