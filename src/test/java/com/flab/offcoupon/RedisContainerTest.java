package com.flab.offcoupon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class RedisContainerTest {
    private static final Logger logger = LoggerFactory.getLogger(RedisContainerTest.class);

//    @Container // Testcontainers에서 제공하는 어노테이션으로 Docker 컨테이너를 생성합니다.
//    private GenericContainer redisContainer = new GenericContainer(DockerImageName.parse("redis:5.0.3-alpine"))
//            .withExposedPorts(6379);
//    public RedisTemplate<String, Object> redisTemplate;
//    public RedisContainerTest() {
//        redisContainer.start();
//        setUp();
//    }
//    void setUp() {
//        System.out.println("오예!!!!!!");
//        // RedisTemplate 설정
//        String redisHost = redisContainer.getHost();
//        System.out.println("redisHost = " + redisHost);
//        Integer redisPort = redisContainer.getFirstMappedPort();
//        System.out.println("redisPort = " + redisPort);
//        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
//        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfig);
//        lettuceConnectionFactory.afterPropertiesSet();
//
//        redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
//        redisTemplate.afterPropertiesSet();
//    }

//    static final String REDIS_IMAGE = "redis:6-alpine";
//    static final GenericContainer REDIS_CONTAINER;
//
//    static {
//        REDIS_CONTAINER = new GenericContainer<>(REDIS_IMAGE)
//                .withExposedPorts(6379)
//                .withReuse(true);
//        REDIS_CONTAINER.start();
//    }
//
//    @DynamicPropertySource
//    public static void overrideProps(DynamicPropertyRegistry registry){
//        registry.add("spring.redis.host", REDIS_CONTAINER::getHost);
//        registry.add("spring.redis.port", () -> ""+REDIS_CONTAINER.getMappedPort(6379));
//        registry.add("spring.redis.password", () -> "");
//    }

    private static final GenericContainer MY_REDIS_CONTAINER;
    static {
        MY_REDIS_CONTAINER = new GenericContainer<>("redis:6")
                .withExposedPorts(6379)
                .withReuse(true);
        MY_REDIS_CONTAINER.start();
        logger.info(">> MY_REDIS_CONTAINER.getHost() = " + MY_REDIS_CONTAINER.getHost());
        logger.info(">> MY_REDIS_CONTAINER.getMappedPort(6379) = " + MY_REDIS_CONTAINER.getMappedPort(6379));
        logger.info(">> MY_REDIS_CONTAINER.getContainerIpAddress() = " + MY_REDIS_CONTAINER.getContainerIpAddress());
        System.setProperty("spring.redis.host", MY_REDIS_CONTAINER.getHost());
        System.setProperty("spring.redis.port", MY_REDIS_CONTAINER.getMappedPort(6379).toString());
        System.setProperty("spring.redis.password", "");
    }
}
