package com.example.j2n.report_srv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Import;
import com.example.j2n.aspect.LoggerAspect;
import com.example.j2n.config.BaseRedisConfig;
import com.example.j2n.messaging.config.BaseRabbitConfig;
import com.example.j2n.messaging.config.RabbitTemplateConfig;

@SpringBootApplication
@Import({ BaseRedisConfig.class, LoggerAspect.class, BaseRabbitConfig.class, RabbitTemplateConfig.class })
public class ReportSrvApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReportSrvApplication.class, args);
	}

}
