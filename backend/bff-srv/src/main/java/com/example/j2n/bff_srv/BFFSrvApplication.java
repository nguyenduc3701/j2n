package com.example.j2n.bff_srv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import org.springframework.context.annotation.Import;
import com.example.j2n.swagger.J2NOpenApiCustomizer;

@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class,
		JdbcTemplateAutoConfiguration.class
})
@Import({ J2NOpenApiCustomizer.class })
public class BFFSrvApplication {

	public static void main(String[] args) {
		SpringApplication.run(BFFSrvApplication.class, args);
	}

}
