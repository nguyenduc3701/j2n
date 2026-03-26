package com.example.j2n.bff_srv.service;

import com.example.j2n.bff_srv.constant.GatewayPath;
import com.example.j2n.bff_srv.controller.request.GetConfigurationsRequest;
import com.example.j2n.bff_srv.controller.request.UpdateConfigurationRequest;
import com.example.j2n.bff_srv.dto.ConfigItemDTO;
import com.example.j2n.bff_srv.utils.RestClientUtil;
import com.example.j2n.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfigurationService {

    private final RestClientUtil restClientUtil;

    public BaseResponse<List<ConfigItemDTO>> getAllConfigurations() {
        return restClientUtil.request(
                GatewayPath.CONFIGURATION_BASE_PATH,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<BaseResponse<List<ConfigItemDTO>>>() {
                });
    }

    public BaseResponse<ConfigItemDTO> getConfigurationByKey(String configKey) {
        String path = String.format(GatewayPath.CONFIGURATION_BY_KEY_PATH, configKey);
        return restClientUtil.request(
                path,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<BaseResponse<ConfigItemDTO>>() {
                });
    }

    public BaseResponse<List<ConfigItemDTO>> getConfigurationsByKeys(GetConfigurationsRequest request) {
        return restClientUtil.request(
                GatewayPath.CONFIGURATION_BY_KEYS_PATH,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<BaseResponse<List<ConfigItemDTO>>>() {
                });
    }

    public BaseResponse<ConfigItemDTO> updateConfiguration(String configKey, UpdateConfigurationRequest request) {
        String path = String.format(GatewayPath.CONFIGURATION_BY_KEY_PATH, configKey);
        return restClientUtil.request(
                path,
                HttpMethod.PUT,
                request,
                new ParameterizedTypeReference<BaseResponse<ConfigItemDTO>>() {
                });
    }
}
