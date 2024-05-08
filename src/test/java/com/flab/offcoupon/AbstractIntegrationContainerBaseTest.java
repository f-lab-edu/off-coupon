package com.flab.offcoupon;

import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ContextConfiguration
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class AbstractIntegrationContainerBaseTest {
//    @Value("${DB_DATABASE}")
//    private static String database;

//    @LocalServerPort
//    int port;
    private static final Logger logger = LoggerFactory.getLogger(AbstractIntegrationContainerBaseTest.class);
    @Container
    static GenericContainer MY_REDIS_CONTAINER =
            new GenericContainer("redis:5.0.3-alpine")
                    .withExposedPorts(6379)
                    .withEnv("REDIS_PASSWORD", "yourRedisPassword"); // 비밀번호 설정
//    @Container
//    static MySQLContainer MY_SQL_CONTAINER = new MySQLContainer("mysql:8");
    @Container
    static RabbitMQContainer MY_RABBITMQ_CONTAINER = new RabbitMQContainer("rabbitmq:3.10.7");

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", MY_REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> MY_REDIS_CONTAINER.getMappedPort(6379));
        registry.add("spring.data.redis.password", () -> MY_REDIS_CONTAINER.getEnvMap().get("REDIS_PASSWORD"));

//        registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
//        registry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
//        registry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);

        registry.add("spring.rabbitmq.host", MY_RABBITMQ_CONTAINER::getHost);
        registry.add("spring.rabbitmq.port", () -> MY_RABBITMQ_CONTAINER.getMappedPort(5672));
        registry.add("spring.rabbitmq.username", MY_RABBITMQ_CONTAINER::getAdminUsername);
        registry.add("spring.rabbitmq.password", MY_RABBITMQ_CONTAINER::getAdminPassword);
    }

    @BeforeEach
    void setUpRabbitMQ() {
        RabbitMQTestInitializer.initializeRabbitMQ(MY_RABBITMQ_CONTAINER);
    }
}