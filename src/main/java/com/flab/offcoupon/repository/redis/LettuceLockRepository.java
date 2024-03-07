package com.flab.offcoupon.repository.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Redis의 Lettuce클라이언트를 사용하여 락을 처리하는 컴포넌트 클래스입니다.
 */
@Component
public class LettuceLockRepository {

    private RedisTemplate<String, String> redisTemplate;

    public LettuceLockRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 락을 획득하는 메서드
     *
     * @param key 락의 대상이 되는 키
     * @return 락 획득 성공 여부 (true: 성공, false: 실패)
     */
    public Boolean lock(Long key) {
        return redisTemplate
                .opsForValue()
                .setIfAbsent(generateKey(key), "lock", Duration.ofSeconds(3_000));
    }
    /**
     * 락을 해제하는 메서드
     *
     * @param key 락의 대상이 되는 키
     * @return 락 해제 성공 여부 (true: 성공, false: 실패)
     */
    public Boolean unlock(Long key) {
        return redisTemplate.delete(generateKey(key));
    }
    /**
     * 락의 키를 생성하는 메서드
     *
     * @param key 락의 대상이 되는 키
     * @return 생성된 락의 키
     */
    private String generateKey(Long key) {
        return key.toString();
    }
}