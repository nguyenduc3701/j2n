package com.example.j2n.config_srv.constant;

import java.util.Set;

import lombok.Getter;

@Getter
public class ConfigConstants {

    private ConfigConstants() {
    }

    private static final Set<String> UNUPDATABLE_CONFIG_KEYS = Set.of(
            "spring.datasource.password",
            "spring.cloud.vault.token");

    public static boolean isUnupdatable(String configKey) {
        return UNUPDATABLE_CONFIG_KEYS.contains(configKey);
    }
}
