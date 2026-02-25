package com.example.j2n.config_srv.service;

import com.example.j2n.config_srv.repository.ServiceRepository;
import com.example.j2n.config_srv.repository.entity.ServiceEntity;
import com.example.j2n.config_srv.service.reponse.ServiceItemResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public List<ServiceEntity> getAllServices() {
        log.info("[Config-srv] Fetching all services");
        return ResponseFactory.success(getAllServicesResponse());
    }

    public Optional<ServiceEntity> findById(String id) {
        return serviceRepository.findById(id);
    }

    public ServiceEntity save(ServiceEntity serviceEntity) {
        return serviceRepository.save(serviceEntity);
    }

    public Optional<ServiceEntity> update(String id, ServiceEntity serviceEntity) {
        if (serviceRepository.existsById(id)) {
            serviceEntity.setId(id);
            return Optional.of(serviceRepository.save(serviceEntity));
        }
        return Optional.empty();
    }

    public boolean delete(String id) {
        if (serviceRepository.existsById(id)) {
            serviceRepository.deleteById(id);
            return true;
        }
        return false;
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
