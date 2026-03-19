package com.example.j2n.report_srv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Import;
import com.example.j2n.aspect.LoggerAspect;
import com.example.j2n.config.BaseRedisConfig;

@SpringBootApplication
@Import({ BaseRedisConfig.class, LoggerAspect.class })
public class ReportSrvApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReportSrvApplication.class, args);
	}

}
