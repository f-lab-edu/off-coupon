package com.flab.offcoupon;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public abstract class AbstractIntegrationContainerBaseTest {

//    private static final GenericContainer MY_REDIS_CONTAINER;
//    private static final GenericContainer MY_RABBITMQ_CONTAINER;
//
//    static {
//        MY_REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:6"))
//                .withExposedPorts(6379);
//        MY_REDIS_CONTAINER.start();
//        System.setProperty("spring.redis.host", MY_REDIS_CONTAINER.getHost());
//        System.setProperty("spring.redis.port", MY_REDIS_CONTAINER.getMappedPort(6379).toString());
//        System.setProperty("spring.redis.password", "");
//
//
//        MY_RABBITMQ_CONTAINER = new GenericContainer(DockerImageName.parse("rabbitmq:3-management"));
//        MY_RABBITMQ_CONTAINER.start();
//        System.setProperty("spring.rabbitmq.host", MY_RABBITMQ_CONTAINER.getHost());
//        System.setProperty("spring.rabbitmq.port", MY_RABBITMQ_CONTAINER.getMappedPort(5672).toString());
//        System.setProperty("spring.rabbitmq.username", "guest");
//        System.setProperty("spring.rabbitmq.password", "guest");
//       // RabbitMQTestInitializer.initializeRabbitMQ(MY_RABBITMQ_CONTAINER);
//    }
}
