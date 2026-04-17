package com.example.j2n.image_srv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Import;

import com.example.j2n.aspect.LoggerAspect;
import com.example.j2n.swagger.J2NOpenApiCustomizer;
import com.example.j2n.messaging.config.BaseRabbitConfig;
import com.example.j2n.messaging.config.RabbitTemplateConfig;

@SpringBootApplication
@Import({ J2NOpenApiCustomizer.class, LoggerAspect.class, BaseRabbitConfig.class, RabbitTemplateConfig.class })
public class ImageSrvApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImageSrvApplication.class, args);
	}

}
