package com.example.j2n.config_srv.controller;

import com.example.j2n.config_srv.service.ServiceService;
import com.example.j2n.config_srv.service.reponse.ServiceItemResponse;
import com.example.j2n.dto.BaseResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/centralize/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<ServiceItemResponse>>> getAllServices() {
        return ResponseEntity.ok(serviceService.getAllServices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ServiceItemResponse>> getServiceById(@PathVariable String id) {
        return ResponseEntity.ok(serviceService.getServiceById(id));
    }
}
