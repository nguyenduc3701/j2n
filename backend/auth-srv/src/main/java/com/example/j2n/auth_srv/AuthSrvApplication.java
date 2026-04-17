package com.example.j2n.auth_srv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Import;
import com.example.j2n.swagger.J2NOpenApiCustomizer;
import com.example.j2n.utils.RedisUtil;
import com.example.j2n.utils.SearchFactory;
import com.example.j2n.aspect.LoggerAspect;
import com.example.j2n.config.BaseRedisConfig;
import com.example.j2n.messaging.config.BaseRabbitConfig;
import com.example.j2n.messaging.config.RabbitTemplateConfig;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@EnableRabbit
@SpringBootApplication
@Import({ J2NOpenApiCustomizer.class, BaseRedisConfig.class, RedisUtil.class, LoggerAspect.class, SearchFactory.class,
		BaseRabbitConfig.class, RabbitTemplateConfig.class })
public class AuthSrvApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthSrvApplication.class, args);
	}

}
