package com.flab.offcoupon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
//@ActiveProfiles("test")
public class AbstractIntegrationContainerBaseTest {
//    @Value("${DB_DATABASE}")
//    private static String database;

    private static final Logger logger = LoggerFactory.getLogger(AbstractIntegrationContainerBaseTest.class);
    private static final GenericContainer MY_REDIS_CONTAINER;
    //private static final MySQLContainer MY_SQL_CONTAINER;
    private static final RabbitMQContainer MY_RABBITMQ_CONTAINER;

    static {
        // 레디스 컨테이너
        MY_REDIS_CONTAINER = new GenericContainer<>("redis:6")
                .withExposedPorts(6379)
                .withReuse(true);
        MY_REDIS_CONTAINER.start();

        logger.info(">> MY_REDIS_CONTAINER.getHost() = " + MY_REDIS_CONTAINER.getHost());
        logger.info(">> MY_REDIS_CONTAINER.getMappedPort(6379) = " + MY_REDIS_CONTAINER.getMappedPort(6379));
        logger.info(">> MY_REDIS_CONTAINER.getContainerIpAddress() = " + MY_REDIS_CONTAINER.getContainerIpAddress());

//        System.setProperty("spring.redis.host", MY_REDIS_CONTAINER.getHost());
//        System.setProperty("spring.redis.port", MY_REDIS_CONTAINER.getMappedPort(6379).toString());
//        System.setProperty("spring.redis.password", "");

        // 레빗엠큐 컨테이너
        MY_RABBITMQ_CONTAINER = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.11.11-alpine"));
        MY_RABBITMQ_CONTAINER.start();

        logger.info(">> MY_RABBITMQ_CONTAINER.getHose() = " + MY_RABBITMQ_CONTAINER.getHost());
        logger.info(">> MY_RABBITMQ_CONTAINER.getMappedPort(5672) = " + MY_RABBITMQ_CONTAINER.getMappedPort(5672));
        logger.info(">> MY_RABBITMQ_CONTAINER.getContainerIpAddress() = " + MY_RABBITMQ_CONTAINER.getContainerIpAddress());

//        System.setProperty("spring.rabbitmq.host", MY_RABBITMQ_CONTAINER.getHost());
//        System.setProperty("spring.rabbitmq.port", MY_RABBITMQ_CONTAINER.getMappedPort(5672).toString());
//        System.setProperty("spring.rabbitmq.username", "guest");
//        System.setProperty("spring.rabbitmq.password", "guest");


        //RabbitMQTestInitializer.initializeRabbitMQ(MY_RABBITMQ_CONTAINER);
    }

//    @BeforeEach
//    void setUpRabbitMQ() {
//        RabbitMQTestInitializer.initializeRabbitMQ(MY_RABBITMQ_CONTAINER);
//    }
}


//        MY_SQL_CONTAINER = new MySQLContainer<>("mysql:8.0.25")
//                .withDatabaseName("off-coupon")
//                .withUsername("root")
//                .withPassword("");
//        MY_SQL_CONTAINER.start();
////
//        logger.info(">> MY_SQL_CONTAINER.getDatabaseName() = " + MY_SQL_CONTAINER.getDatabaseName());
//        logger.info(">> MY_SQL_CONTAINER.getJdbcUrl() = " + MY_SQL_CONTAINER.getJdbcUrl());
//        logger.info(">> MY_SQL_CONTAINER.getUsername() = " + MY_SQL_CONTAINER.getUsername());
//        logger.info(">> MY_SQL_CONTAINER.getPassword() = " + MY_SQL_CONTAINER.getPassword());
//
//        System.setProperty("spring.datasource.driver-class-name", MY_SQL_CONTAINER.getDatabaseName());
//        System.setProperty("spring.datasource.url", MY_SQL_CONTAINER.getJdbcUrl());
//        System.setProperty("spring.datasource.username", MY_SQL_CONTAINER.getUsername());
//        System.setProperty("spring.datasource.password", MY_SQL_CONTAINER.getPassword());
