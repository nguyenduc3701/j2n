package com.example.j2n.bff_srv.api_gateway_srv.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/gateway/health")
    public String health() {
        return "gateway OK";
    }
}

