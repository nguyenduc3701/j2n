package com.example.j2n.bff_srv.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/bff")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String helloWorld(){
        return "hello";
    }
}
