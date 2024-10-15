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
	 * Redis DEL 명령어 : 대기 큐에서 특정 키를 제거합니다.
	 * @param key 대기 큐의 키
	 * @return 대기 큐에서 해당 키가 제거됐는지 여부
	 */
	public Boolean delete(String key) {
		return redisTemplate.delete(key);
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

}
