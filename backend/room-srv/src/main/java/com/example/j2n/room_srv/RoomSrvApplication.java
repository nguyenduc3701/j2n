package com.example.j2n.room_srv;

import com.example.j2n.aspect.LoggerAspect;
import com.example.j2n.swagger.J2NOpenApiCustomizer;
import com.example.j2n.utils.SearchFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableDiscoveryClient
@Import({ J2NOpenApiCustomizer.class, LoggerAspect.class, SearchFactory.class })
public class RoomSrvApplication {

    public static void main(String[] args) {
        SpringApplication.run(RoomSrvApplication.class, args);
    }

}
