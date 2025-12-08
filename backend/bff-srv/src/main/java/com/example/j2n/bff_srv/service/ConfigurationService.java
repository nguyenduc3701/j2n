package com.example.j2n.bff_srv.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.j2n.bff_srv.repository.entity.ConfigurationEntity;
import com.example.j2n.bff_srv.constant.ConfigurationKeys;
import com.example.j2n.bff_srv.repository.ConfigurationRepository;
import com.example.j2n.bff_srv.service.response.BaseResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConfigurationService {
    private final ConfigurationRepository configurationRepository;
    private final List<String> globalKeys = ConfigurationKeys.GLOBAL_KEYS;
    private final List<String> portfolioKeys = ConfigurationKeys.PORTFOLIO_KEYS;

    public BaseResponse<List<ConfigurationEntity>> getPortfolioConfigurations() {
        List<ConfigurationEntity> configurations = configurationRepository.findByKeyIn(portfolioKeys);
        return BaseResponse.success(configurations);
    }

    public BaseResponse<List<ConfigurationEntity>> getGlobalConfigurations() {
        List<ConfigurationEntity> configurations = configurationRepository.findByKeyIn(globalKeys);
        return BaseResponse.success(configurations);
    }
}
