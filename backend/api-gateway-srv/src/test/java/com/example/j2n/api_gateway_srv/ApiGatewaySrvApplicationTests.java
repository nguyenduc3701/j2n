package com.example.j2n.api_gateway_srv;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@org.junit.jupiter.api.Disabled
@SpringBootTest
class ApiGatewaySrvApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void main() {
		assertDoesNotThrow(
				() -> ApiGatewaySrvApplication.main(new String[] { "--spring.main.web-application-type=reactive" }));
	}
}
