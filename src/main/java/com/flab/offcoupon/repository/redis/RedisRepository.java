package com.flab.offcoupon.repository.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Redis SADD 명령어: 쿠폰 발급 요청의 고유성을 유지하고 발급 수량을 제어하기 위해 사용됩니다.
     *
     * @param key   Set의 키
     * @param value Set에 추가할 값
     * @return 추가된 멤버 수
     */
    public Long sAdd(String key, String value) {
        return redisTemplate.opsForSet().add(key, value);
    }

    /**
     * Redis SCARD 명령어: Set의 크기를 반환합니다.
     *
     * @param key Set의 키
     * @return Set의 크기
     */
    public Long sCard(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * Redis SISMEMBER 명령어: Set에 특정 멤버가 존재하는지 확인합니다.
     *
     * @param key   Set의 키
     * @param value 확인할 멤버
     * @return 멤버의 존재 여부
     */
    public Boolean sIsMember(String key, String value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * Redis RPUSH 명령어: 주어진 리스트의 끝에 하나 이상의 값을 추가함으로써 쿠폰 발급의 대기 큐로서 사용됩니다.
     *
     * @param key   대기 큐의 키
     * @param value 대기 큐에 추가할 값
     * @return 대기 큐에 추가된 요소의 총 개수
     */
    public Long rPush(String key, String value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * Redis DEL 명령어 : 대기 큐에서 특정 키를 제거합니다.
     * @param key 대기 큐의 키
     * @return 대기 큐에서 해당 키가 제거됐는지 여부
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }
}
