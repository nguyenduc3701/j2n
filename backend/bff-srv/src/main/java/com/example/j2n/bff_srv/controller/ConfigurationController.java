package com.example.j2n.bff_srv.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.List;
import com.example.j2n.bff_srv.repository.entity.ConfigurationEntity;
import com.example.j2n.bff_srv.service.ConfigurationService;
import com.example.j2n.dto.BaseResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.MediaType;
import io.swagger.v3.oas.annotations.Operation;
import com.example.j2n.swagger.annotation.*;
import com.example.j2n.enums.BaseMessageEnum;

@RestController
@RequestMapping("api/bff/configuration")
@Slf4j
@RequiredArgsConstructor
public class ConfigurationController {

        private final ConfigurationService configurationService;

        @GetMapping(value = "/portfolio", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Get portfolio configurations", description = "Fetch all configuration settings related to the portfolio")
        @J2NApiResponses({
                        @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                                        @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
                        })
        })
        public ResponseEntity<BaseResponse<List<ConfigurationEntity>>> getPortfolioConfigurations() {
                BaseResponse<List<ConfigurationEntity>> response = configurationService.getPortfolioConfigurations();
                return ResponseEntity.ok(response);
        }

        @GetMapping(value = "/global", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Get global configurations", description = "Fetch all global configuration settings")
        @J2NApiResponses({
                        @J2NApiResponse(httpCode = 200, description = BaseMessageEnum.BaseMessageConstants.SUCCESS, examples = {
                                        @J2NApiExample(baseResponseStatus = BaseMessageEnum.SUCCESS)
                        })
        })
        public ResponseEntity<BaseResponse<List<ConfigurationEntity>>> getGlobalConfigurations() {
                BaseResponse<List<ConfigurationEntity>> response = configurationService.getGlobalConfigurations();
                return ResponseEntity.ok(response);
        }
}
