package com.example.j2n.travel_srv.service;

import com.example.j2n.aspect.LogAround;
import com.example.j2n.dto.BaseResponse;
import com.example.j2n.utils.ResponseFactory;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TravelService {

    @LogAround(message = "Getting hello message")
    public BaseResponse<String> getHelloMessage() {
        return ResponseFactory.success("Hello from travel-srv service layer with ResponseFactory!");
    }
}
