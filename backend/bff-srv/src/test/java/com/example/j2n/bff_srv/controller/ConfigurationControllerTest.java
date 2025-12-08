package com.example.j2n.bff_srv.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.j2n.bff_srv.repository.entity.ConfigurationEntity;
import com.example.j2n.bff_srv.service.ConfigurationService;
import com.example.j2n.bff_srv.service.response.BaseResponse;

@ExtendWith(MockitoExtension.class)
class ConfigurationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ConfigurationService configurationService;

    @InjectMocks
    private ConfigurationController configurationController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(configurationController).build();
    }

    @Test
    void getPortfolioConfigurations_ShouldReturnPortfolioConfigs() throws Exception {
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

        List<ConfigurationEntity> configurations = Arrays.asList(config1, config2);
        when(configurationService.getPortfolioConfigurations())
                .thenReturn(BaseResponse.success(configurations));

        // Act & Assert
        mockMvc.perform(get("/api/bff/configuration/portfolio")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].key").value("portfolio.name"))
                .andExpect(jsonPath("$.data[0].value").value("Jadon Nguyen"))
                .andExpect(jsonPath("$.data[1].key").value("portfolio.pre-introduction"))
                .andExpect(jsonPath("$.data[1].value").value("Pre Introduction"));
    }

    @Test
    void getGlobalConfigurations_ShouldReturnGlobalConfigs() throws Exception {
        // Arrange
        ConfigurationEntity config1 = new ConfigurationEntity();
        config1.setId(3L);
        config1.setKey("store-portal.base-url");
        config1.setValue("http://localhost:3104");
        config1.setDescription("Store portal base URL");
        config1.setCreatedAt(LocalDateTime.now());
        config1.setUpdatedAt(LocalDateTime.now());
        config1.setIsDeleted(false);

        ConfigurationEntity config2 = new ConfigurationEntity();
        config2.setId(4L);
        config2.setKey("room-portal.base-url");
        config2.setValue("http://localhost:3103");
        config2.setDescription("Room portal base URL");
        config2.setCreatedAt(LocalDateTime.now());
        config2.setUpdatedAt(LocalDateTime.now());
        config2.setIsDeleted(false);

        List<ConfigurationEntity> configurations = Arrays.asList(config1, config2);
        when(configurationService.getGlobalConfigurations())
                .thenReturn(BaseResponse.success(configurations));

        // Act & Assert
        mockMvc.perform(get("/api/bff/configuration/global")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].key").value("store-portal.base-url"))
                .andExpect(jsonPath("$.data[0].value").value("http://localhost:3104"))
                .andExpect(jsonPath("$.data[1].key").value("room-portal.base-url"))
                .andExpect(jsonPath("$.data[1].value").value("http://localhost:3103"));
    }
}
