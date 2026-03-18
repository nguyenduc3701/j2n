package com.example.j2n.auth_srv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Import;
import com.example.j2n.swagger.J2NOpenApiCustomizer;

@SpringBootApplication
@Import({ J2NOpenApiCustomizer.class })
public class AuthSrvApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthSrvApplication.class, args);
	}

}
