package com.example.j2n.product_srv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.example.j2n.aspect.LoggerAspect;
import com.example.j2n.messaging.config.BaseRabbitConfig;
import com.example.j2n.messaging.config.RabbitTemplateConfig;
import com.example.j2n.config.BaseRedisConfig;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@EnableRabbit
@SpringBootApplication
@Import({ LoggerAspect.class, BaseRabbitConfig.class, RabbitTemplateConfig.class, BaseRedisConfig.class })
public class ProductSrvApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductSrvApplication.class, args);
	}

}
