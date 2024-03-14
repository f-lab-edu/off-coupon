package com.flab.offcoupon.config.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession
public class SessionConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private String redisPort;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    private static final String REDISSON_HOST_PREFIX = "redis://";
    /**
     * <p>Lettuce: Lettuce는 Redis와의 비동기 및 논 블로킹 I/O를 지원하는 자바 라이브러리입니다.</p>
     * 해당 빈은 세션 저장소로 사용하기 위해 LettuceConnectionFactory를 생성합니다.<br>
     * <br>
     * lettuce-core 라이브러리는 내부적으로 164번 라인에서 URI_SCHEME_REDIS 상수를 사용하여 "redis://"인 protocol을 붙여줍니다.
     * 따라서, 해당 라이브러리를 사용할 때는 "redis://"를 붙여주지 않아도 됩니다.
     * @see <a href="https://github.com/lettuce-io/lettuce-core/blob/main/src/main/java/io/lettuce/core/RedisURI.java#L164"> [Lettuce-core 공식문서의 Redis URI] </a>
    */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisHost);
        redisStandaloneConfiguration.setPort(Integer.parseInt(redisPort));
        redisStandaloneConfiguration.setPassword(redisPassword);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    /**
     * <p>Redisson : Redisson은 Redis Java 클라이언트 및 쓰레딩 프레임워크를 제공하는 오픈 소스입니다.</p>
     * 해당 빈은 분산 락을 처리하기 위해 redissonClient를 생성합니다.<br>
     * <br>
     * redisson 라이브러리는 내부적으로 protocol을 설정해주지 않습니다.<br>
     * 따라서, 해당 라이브러리를 사용할 때는 별도로 "redis://" PREFIX를 붙어줘야 합니다.
     *
     * @see <a href="https://github.com/redisson/redisson"> [redisson 공식문서의 Read Me의 Java부분] </a>
     */

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(REDISSON_HOST_PREFIX + redisHost + ":" + Integer.parseInt(redisPort))
                .setPassword(redisPassword)
                .setSslEnableEndpointIdentification(false);
        return Redisson.create(config);
    }

    /**
     * Redis template
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(springSessionDefaultRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer(){
        return new Jackson2JsonRedisSerializer<>(Object.class);
    }
}