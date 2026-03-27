package com.example.j2n.travel_srv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.example.j2n.aspect.LoggerAspect;

@SpringBootApplication
@Import({ LoggerAspect.class })
public class TravelSrvApplication {

	public static void main(String[] args) {
		SpringApplication.run(TravelSrvApplication.class, args);
	}

}
