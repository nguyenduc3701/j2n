package com.example.j2n.notification_srv;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
    "spring.cloud.config.enabled=false",
    "spring.config.import=",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration"
})
@ActiveProfiles("test")
class NotificationSrvApplicationTests {

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private com.example.j2n.notification_srv.repository.NotificationRepository notificationRepository;

    @Test
    void contextLoads() {
    }

}
