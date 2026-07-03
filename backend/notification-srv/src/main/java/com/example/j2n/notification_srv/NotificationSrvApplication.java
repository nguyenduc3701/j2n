package com.example.j2n.notification_srv;

import com.example.j2n.aspect.LoggerAspect;
import com.example.j2n.messaging.config.BaseRabbitConfig;
import com.example.j2n.messaging.config.RabbitTemplateConfig;
import com.example.j2n.swagger.J2NOpenApiCustomizer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ J2NOpenApiCustomizer.class, LoggerAspect.class, BaseRabbitConfig.class, RabbitTemplateConfig.class })
public class NotificationSrvApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationSrvApplication.class, args);
    }

}
