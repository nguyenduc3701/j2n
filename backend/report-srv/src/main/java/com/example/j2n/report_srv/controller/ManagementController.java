package com.example.j2n.report_srv.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report/management")
public class ManagementController {

    @GetMapping
    public String hello() {
        return "Hello World from Report Service!";
    }
}
