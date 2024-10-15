package com.flab.offcoupon.service.coupon_issue.async;

import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.*;
import static com.flab.offcoupon.util.RedisKeyUtils.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.flab.offcoupon.exception.coupon.CouponQuantityException;
import com.flab.offcoupon.exception.coupon.DuplicatedCouponException;
import com.flab.offcoupon.repository.redis.RedisRepository;
import com.flab.offcoupon.service.coupon_issue.async.dto.EventAndCouponCache;

import lombok.RequiredArgsConstructor;

/**
 * 비동기 쿠폰 발급 시 Redis를 사용하여 수량 및 중복 여부를 확인하는 서비스 클래스입니다.
 */
@RequiredArgsConstructor
@Service
public class CouponIssueRedisService {

	private static final int AVAILABLE_COUPON_COUNT_PER_USER = 1;
	private static final int NO_TTL_SET_UP = -1;

	private final RedisRepository redisRepository;

	/**
	 * 쿠폰 발급 가능 여부 및 중복 발급 여부를 확인합니다.
	 * 발급 가능 여부 확인후 TTL을 설정합니다.
	 *
	 * @param eventAndCouponCache   이벤트와 쿠폰의 캐싱 정보
	 * @param memberId 발급 요청을 하는 회원의 ID
	 * @throws CouponQuantityException    발급 가능 수량 초과 시 발생하는 예외
	 * @throws DuplicatedCouponException  중복 발급 요청 시 발생하는 예외
	 */
	public void checkCouponIssueQuantityAndDuplicate(final EventAndCouponCache eventAndCouponCache,
		final long memberId) {

		String accumulatedCouponKey = getIssueRequestKey(eventAndCouponCache.couponCache().id());
		String duplicatedUserCouponKey = getCouponIssueRequestForDuplicatedCouponKey(
			eventAndCouponCache.couponCache().id(), memberId);

		if (!availableTotalIssueQuantity(accumulatedCouponKey, eventAndCouponCache.couponCache().maxQuantity())) {
			throw new CouponQuantityException(
				ASYNC_INVALID_COUPON_QUANTITY.formatted(eventAndCouponCache.couponCache().id()));
		}
		if (!availableUserIssueQuantity(duplicatedUserCouponKey)) {
			throw new DuplicatedCouponException(
				ASYNC_DUPLICATED_COUPON.formatted(memberId, eventAndCouponCache.couponCache().id()));
		}

		long ttl = getTTLFromDailyIssuedEndTime(
			eventAndCouponCache.eventCache().dailyIssueEndTime());
		setTTLForKeys(ttl, accumulatedCouponKey, duplicatedUserCouponKey);
	}

	/**
	 * increment 명령어를 통해  누적 쿠폰 발행 수량을 카운팅하고, 총 발급 가능 수량과 비교합니다.
	 *
	 * @param key 누적 발행 쿠폰 수량을 저장하는 키
	 * @param maxQuantity 발급 가능한 최대 수량
	 * @return 발급 가능 여부
	 */
	public boolean availableTotalIssueQuantity(final String key, final Long maxQuantity) {
		return redisRepository.increment(key) <= maxQuantity;
	}

	/**
	 * increment 명령어를 통해 중복 발급 여부를 확인합니다.
	 * 한 사람당 1번만 발급이 가능하다는 전제 하에 상수는 1로 설정합니다.
	 *
	 * @param key 중복 발급 여부를 확인하는 키
	 * @return 중복 발급 가능 여부
	 */
	public boolean availableUserIssueQuantity(final String key) {
		return redisRepository.increment(key) <= AVAILABLE_COUPON_COUNT_PER_USER;
	}

	/**
	 * 일일 발급 종료 시간을 기준으로 TTL을 계산합니다.
	 * TTL은 현재 시간부터 일일 발급 종료 시간까지의 남은 시간을 초 단위로 반환합니다.
	 * @param dailyIssueEndTime 일일 발급 종료 시간 ex. "23:00"
	 * @return TTL
	 */
	private long getTTLFromDailyIssuedEndTime(final String dailyIssueEndTime) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime endOfDayIssueTime = now.toLocalDate().atTime(LocalTime.parse(dailyIssueEndTime)).plusMinutes(1);
		return Duration.between(now, endOfDayIssueTime).getSeconds();
	}

	/**
	 * 여러 키에 대해 TTL을 설정하는 메서드
	 * @param ttl TTL 값
	 * @param keys 설정할 키들
	 **/
	private void setTTLForKeys(final long ttl, String... keys) {
		for (String key : keys) {
			setCacheTtlAbout(key, ttl);
		}
	}

	/**
	 * 캐시의 TTL을 설정합니다.
	 * @param key 캐시 키
	 * @param ttl TTL
	 **/
	public void setCacheTtlAbout(final String key, final long ttl) {
		if (redisRepository.getTTL(key) == NO_TTL_SET_UP) {
			redisRepository.setTTL(key, ttl, TimeUnit.SECONDS);
		}
	}
}
