package com.example.j2n.api_gateway_srv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.example.j2n.config.BaseRedisConfig;
import com.example.j2n.utils.RedisUtil;

@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class,
		RabbitAutoConfiguration.class
})
@EnableScheduling
@Import({ BaseRedisConfig.class, RedisUtil.class })
public class ApiGatewaySrvApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewaySrvApplication.class, args);
	}

}
