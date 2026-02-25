package com.example.j2n.config_srv.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "configuration")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "config_key", nullable = false)
    private String configKey;

    @Column(name = "config_value", columnDefinition = "TEXT")
    private String configValue;

    @Column(name = "service_id", nullable = false)
    private String serviceId;

    @Column(name = "environment", nullable = false)
    private String environment;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
