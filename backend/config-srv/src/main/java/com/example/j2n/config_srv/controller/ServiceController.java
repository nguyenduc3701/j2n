package com.example.j2n.config_srv.controller;

import com.example.j2n.config_srv.repository.entity.ServiceEntity;
import com.example.j2n.config_srv.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/centralize")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @GetMapping("/services")
    public ResponseEntity<List<ServiceEntity>> getAllServices() {
        return ResponseEntity.ok(serviceService.getAllServices());
    }

    @GetMapping("/services/{id}")
    public ResponseEntity<ServiceEntity> getServiceById(@PathVariable String id) {
        return serviceService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
