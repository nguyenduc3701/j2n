package com.example.j2n.config_srv.constant;

import java.util.Set;

import lombok.Getter;

@Getter
public class ConfigConstants {

    private ConfigConstants() {
    }

    private static final Set<String> UNUPDATABLE_CONFIG_KEYS = Set.of(
            "spring.datasource.password",
            "spring.cloud.vault.token",
            "application.rabbitmq.host",
            "application.rabbitmq.port",
            "application.rabbitmq.username",
            "application.rabbitmq.password",
            "application.database.base-url",
            "application.database.username",
            "application.database.password",
            "application.redis.host",
            "application.redis.port",
            "application.redis.password",
            "application.api-gateway.base-url",
            "application.config.base-url",
            "api-gateway.auth.base-url",
            "api-gateway.report.base-url",
            "api-gateway.image.base-url",
            "api-gateway.bff.base-url",
            "image.minio.url",
            "image.minio.access-key",
            "image.minio.secret-key",
            "bff.base-url");

    public static boolean isUnupdatable(String configKey) {
        return UNUPDATABLE_CONFIG_KEYS.contains(configKey);
    }
}
