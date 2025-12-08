package com.example.j2n.bff_srv.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.j2n.bff_srv.repository.entity.ConfigurationEntity;

public interface ConfigurationRepository extends JpaRepository<ConfigurationEntity, Long> {
    Optional<ConfigurationEntity> findByKey(String key);

    List<ConfigurationEntity> findByKeyIn(List<String> keys);
}
