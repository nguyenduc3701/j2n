package com.example.j2n.bff_srv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.j2n.bff_srv.repository.ConfigurationRepository;
import com.example.j2n.bff_srv.repository.entity.ConfigurationEntity;
import com.example.j2n.bff_srv.service.response.BaseResponse;

@ExtendWith(MockitoExtension.class)
class ConfigurationServiceTest {

    @Mock
    private ConfigurationRepository configurationRepository;

    @InjectMocks
    private ConfigurationService configurationService;

    @Test
    void getPortfolioConfigurations_ShouldReturnPortfolioConfigs() {
        // Arrange
        ConfigurationEntity config1 = new ConfigurationEntity();
        config1.setId(1L);
        config1.setKey("portfolio.name");
        config1.setValue("Jadon Nguyen");
        config1.setDescription("Portfolio name");
        config1.setCreatedAt(LocalDateTime.now());
        config1.setUpdatedAt(LocalDateTime.now());
        config1.setIsDeleted(false);

        ConfigurationEntity config2 = new ConfigurationEntity();
        config2.setId(2L);
        config2.setKey("portfolio.pre-introduction");
        config2.setValue("Pre Introduction");
        config2.setDescription("Pre introduction text");
        config2.setCreatedAt(LocalDateTime.now());
        config2.setUpdatedAt(LocalDateTime.now());
        config2.setIsDeleted(false);

        ConfigurationEntity config3 = new ConfigurationEntity();
        config3.setId(3L);
        config3.setKey("portfolio.main-introduction");
        config3.setValue("Main Introduction");
        config3.setDescription("Main introduction text");
        config3.setCreatedAt(LocalDateTime.now());
        config3.setUpdatedAt(LocalDateTime.now());
        config3.setIsDeleted(false);

        List<ConfigurationEntity> mockConfigurations = Arrays.asList(config1, config2, config3);
        when(configurationRepository.findByKeyIn(anyList())).thenReturn(mockConfigurations);

        // Act
        BaseResponse<List<ConfigurationEntity>> response = configurationService.getPortfolioConfigurations();

        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(3, response.getData().size());
        assertEquals("portfolio.name", response.getData().get(0).getKey());
        assertEquals("Jadon Nguyen", response.getData().get(0).getValue());
        assertEquals("portfolio.pre-introduction", response.getData().get(1).getKey());
        assertEquals("Pre Introduction", response.getData().get(1).getValue());
        assertEquals("portfolio.main-introduction", response.getData().get(2).getKey());
        assertEquals("Main Introduction", response.getData().get(2).getValue());

        verify(configurationRepository).findByKeyIn(anyList());
    }

    @Test
    void getGlobalConfigurations_ShouldReturnGlobalConfigs() {
        // Arrange
        ConfigurationEntity config1 = new ConfigurationEntity();
        config1.setId(4L);
        config1.setKey("store-portal.base-url");
        config1.setValue("http://localhost:3104");
        config1.setDescription("Store portal base URL");
        config1.setCreatedAt(LocalDateTime.now());
        config1.setUpdatedAt(LocalDateTime.now());
        config1.setIsDeleted(false);

        ConfigurationEntity config2 = new ConfigurationEntity();
        config2.setId(5L);
        config2.setKey("room-portal.base-url");
        config2.setValue("http://localhost:3103");
        config2.setDescription("Room portal base URL");
        config2.setCreatedAt(LocalDateTime.now());
        config2.setUpdatedAt(LocalDateTime.now());
        config2.setIsDeleted(false);

        ConfigurationEntity config3 = new ConfigurationEntity();
        config3.setId(6L);
        config3.setKey("management-portal.base-url");
        config3.setValue("http://localhost:3102");
        config3.setDescription("Management portal base URL");
        config3.setCreatedAt(LocalDateTime.now());
        config3.setUpdatedAt(LocalDateTime.now());
        config3.setIsDeleted(false);

        List<ConfigurationEntity> mockConfigurations = Arrays.asList(config1, config2, config3);
        when(configurationRepository.findByKeyIn(anyList())).thenReturn(mockConfigurations);

        // Act
        BaseResponse<List<ConfigurationEntity>> response = configurationService.getGlobalConfigurations();

        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(3, response.getData().size());
        assertEquals("store-portal.base-url", response.getData().get(0).getKey());
        assertEquals("http://localhost:3104", response.getData().get(0).getValue());
        assertEquals("room-portal.base-url", response.getData().get(1).getKey());
        assertEquals("http://localhost:3103", response.getData().get(1).getValue());
        assertEquals("management-portal.base-url", response.getData().get(2).getKey());
        assertEquals("http://localhost:3102", response.getData().get(2).getValue());

        verify(configurationRepository).findByKeyIn(anyList());
    }
}
