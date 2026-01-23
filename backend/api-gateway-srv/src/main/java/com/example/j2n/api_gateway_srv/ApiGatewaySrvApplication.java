package com.example.j2n.api_gateway_srv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.example.j2n.config.BaseRedisConfig;
import com.example.j2n.utils.RedisUtil;

@SpringBootApplication
@Import({ BaseRedisConfig.class, RedisUtil.class })
public class ApiGatewaySrvApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewaySrvApplication.class, args);
	}

}
