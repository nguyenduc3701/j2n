package com.example.j2n.order_srv;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.example.j2n.aspect.LoggerAspect;
import com.example.j2n.config.BaseRedisConfig;
import com.example.j2n.messaging.config.BaseRabbitConfig;
import com.example.j2n.messaging.config.RabbitTemplateConfig;
import com.example.j2n.swagger.J2NOpenApiCustomizer;
import com.example.j2n.utils.RedisUtil;
import com.example.j2n.utils.SearchFactory;

@EnableRabbit
@SpringBootApplication
@Import({ J2NOpenApiCustomizer.class, BaseRedisConfig.class, RedisUtil.class, LoggerAspect.class, SearchFactory.class,
		BaseRabbitConfig.class, RabbitTemplateConfig.class })
public class OrderSrvApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderSrvApplication.class, args);
	}

}
