package com.example.j2n.config_srv.service;

import com.example.j2n.config_srv.constant.MessageEnum;
import com.example.j2n.config_srv.repository.ServiceRepository;
import com.example.j2n.config_srv.repository.entity.ServiceEntity;
import com.example.j2n.config_srv.service.reponse.ServiceItemResponse;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.exception.DataNotFoundException;
import com.example.j2n.exception.InvalidInputException;
import com.example.j2n.utils.ResponseFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public BaseResponse<List<ServiceItemResponse>> getAllServices() {
        log.info("[Config-srv] Fetching all services");
        return ResponseFactory.success(getAllServicesResponse());
    }

    public BaseResponse<ServiceItemResponse> getServiceById(String id) {
        log.info("[Config-srv] Fetching service by ID: {}", id);
        ServiceEntity serviceEntity = findByIdOrThrow(id);
        ServiceItemResponse response = buildServiceItemResponse(serviceEntity);
        log.info("[Config-srv] End fetching service by ID. Retrieved service: {}", response.getName());
        return ResponseFactory.success(response);
    }

    public ServiceEntity findByIdOrThrow(String id) {
        log.info("[Config-srv] Fetching service by ID: {}", id);
        if (id == null) {
            throw new InvalidInputException(MessageEnum.INVALID_REQUEST);
        }
        return serviceRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(MessageEnum.SERVICE_NOT_FOUND));
    }

    public List<ServiceItemResponse> getAllServicesResponse() {
        log.info("[Config-srv] Fetching all services");
        return serviceRepository.findAll().stream().map(this::buildServiceItemResponse).toList();
    }

    private ServiceItemResponse buildServiceItemResponse(ServiceEntity serviceEntity) {
        log.info("[Config-srv] Building service item response");
        ServiceItemResponse serviceItemResponse = new ServiceItemResponse();
        serviceItemResponse.setId(serviceEntity.getId());
        serviceItemResponse.setName(serviceEntity.getName());
        return serviceItemResponse;
    }
}
