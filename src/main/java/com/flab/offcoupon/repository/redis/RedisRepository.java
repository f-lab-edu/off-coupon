package com.flab.offcoupon.repository.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class RedisRepository {

	private final RedisTemplate<String, Object> redisTemplate;

	/**
	 * Redis INCR 명령어: 키의 값을 1 증가시킵니다.
	 * @param key 카운팅할 키
	 * @return 카운팅된 값
	 */
	public Long increment(String key) {
		return redisTemplate.opsForValue().increment(key);
	}

	/**
	 * TTL(Time To Live)을 조회합니다.
	 * @param key TTL을 조회할 키
	 **/
	public Long getTTL(String key) {
		return redisTemplate.getExpire(key);
	}

	/**
	 * TTL(Time To Live)을 설정합니다.
	 * @param key TTL을 설정할 키
	 * @param timeout TTL 시간
	 * @param timeUnit TTL 단위
	 **/
	public void setTTL(String key, long timeout, TimeUnit timeUnit) {
		redisTemplate.expire(key, timeout, timeUnit);
	}

	/**
	 * Redis PUBLISH 명령어: 메시지를 특정 채널로 발행합니다.
	 * @param topic 채널
	 * @param message 발행할 메시지
	 */
	public void publish(String topic, Object message) {
		redisTemplate.convertAndSend(topic, message);
	}

	/**
	 * Redis GET 명령어 : 특정 키에 대한 값을 조회합니다.[테스트 코드에서 사용]
	 * @param key 조회할 키
	 * @return 조회된 값
	 */
	public Object get(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	/**
	 * Redis DEL 명령어 : 특정 키에 대한 값을 삭제합니다.[테스트 코드에서 사용]
	 * @param key 삭제할 키
	 * @return 삭제 성공 여부
	 */
	public Boolean delete(String key) {
		return redisTemplate.delete(key);
	}

}
