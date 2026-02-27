package com.example.j2n.report_srv.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import com.example.j2n.constants.CommonConst;

@Configuration
public class RedisCacheConfig {

        @Value("${report.cache.time-minutes}")
        private int cacheTimeMinutes;

        @Bean
        public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory,
                        RedisCacheConfiguration defaultCacheConfiguration) {

                Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
                cacheConfigurations.put(CommonConst.DASHBOARD_CACHE_KEY,
                                defaultCacheConfiguration.entryTtl(Duration.ofMinutes(cacheTimeMinutes)));

                return RedisCacheManager.builder(connectionFactory)
                                .cacheDefaults(defaultCacheConfiguration)
                                .withInitialCacheConfigurations(cacheConfigurations)
                                .build();
        }
}
