package com.flab.offcoupon.service.coupon_issue.async;

import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.*;
import static com.flab.offcoupon.util.RedisKeyUtils.*;

import java.util.List;

import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import com.flab.offcoupon.domain.redis.CouponRedisEntity;
import com.flab.offcoupon.exception.coupon.CouponQuantityException;
import com.flab.offcoupon.exception.coupon.DuplicatedCouponException;
import com.flab.offcoupon.repository.redis.RedisRepository;

import lombok.RequiredArgsConstructor;

/**
 * 비동기 쿠폰 발급 시 Redis를 사용하여 수량 및 중복 여부를 확인하는 서비스 클래스입니다.
 */
@RequiredArgsConstructor
@Service
public class CouponIssueRedisService {

	private static final int AVAILABLE_COUPON_COUNT_PER_USER = 1;

	private final RedisRepository redisRepository;

	public void checkCouponIssueQuantityAndDuplicateV2(CouponRedisEntity coupon, long memberId) {
		String issueRequestKey = getIssueRequestKey(coupon.id());
		RedisScript<String> stringRedisScript = issueRequestScript();

		Object result = redisRepository.execute(stringRedisScript, List.of(issueRequestKey), memberId,
			coupon.id(), coupon.maxQuantity());
		System.out.println("script 결과 : " + result);
		validateScriptResult(coupon, memberId, result);
	}

	private static void validateScriptResult(CouponRedisEntity coupon, long memberId, Object result) {
		System.out.println("script 결과 : " + result);
		switch (result.toString()) {
			case "-1":
				throw new CouponQuantityException(ASYNC_INVALID_COUPON_QUANTITY.formatted(coupon.id()));
			case "-2":
				throw new DuplicatedCouponException(ASYNC_DUPLICATED_COUPON.formatted(memberId, coupon.id()));
		}
	}

	public RedisScript<String> issueRequestScript() {

		String script = "local redisKey = KEYS[1] " + "local memberIdStr = ARGV[1] " + "local couponIdStr = ARGV[2] "
			+ "local maxQuantityStr = ARGV[3] " +

			"local memberId = tonumber(memberIdStr) " + "local couponId = tonumber(couponIdStr) "
			+ "local maxQuantity = tonumber(maxQuantityStr) " +

			"local currentQuantity = redis.call('SCARD', redisKey) " +

			"if currentQuantity == nil then currentQuantity = 0 end " + "if currentQuantity >= maxQuantity then " +
			// 쿠폰발급 수량 증가
			"    return string.format('%d',-1) " + "end " +

			"local isMember = redis.call('SISMEMBER', redisKey, memberIdStr) " + "if isMember < 1 then "
			+ "    redis.call('SADD', redisKey, memberIdStr) " + // 추가
			"else " + "    return string.format('%d',-2) " + "end " + "return string.format('%d',1)";

		return RedisScript.of(script, String.class);
	}

	/**
	 * 쿠폰 발급 가능 여부 및 중복 발급 여부를 확인합니다.
	 *
	 * @param coupon   발급할 쿠폰 정보
	 * @param memberId 발급 요청을 하는 회원의 ID
	 * @throws CouponQuantityException    발급 가능 수량 초과 시 발생하는 예외
	 * @throws DuplicatedCouponException  중복 발급 요청 시 발생하는 예외
	 */
	public void checkCouponIssueQuantityAndDuplicate(final CouponRedisEntity coupon, final long memberId) {
		if (!availableTotalIssueQuantity(coupon.maxQuantity(), coupon.id())) {
			throw new CouponQuantityException(ASYNC_INVALID_COUPON_QUANTITY.formatted(coupon.id()));
		}
		if (!availableUserIssueQuantity(coupon.id(), memberId)) {
			throw new DuplicatedCouponException(ASYNC_DUPLICATED_COUPON.formatted(memberId, coupon.id()));
		}
	}

	/**
	 * 누적 쿠폰 발행 수량을 카운팅하고, 총 발급 가능 수량과 비교합니다.
	 *
	 * @param maxQuantity 발급 가능한 최대 수량
	 * @param couponId    쿠폰 ID
	 * @return 발급 가능 여부
	 */
	public boolean availableTotalIssueQuantity(final Long maxQuantity, final long couponId) {
		String key = getIssueRequestKey(couponId);
		return redisRepository.increment(key) <= maxQuantity;
	}

	/**
	 * increment 명령어를 통해 쿠폰 발급 요청을 카운팅하고, 중복 발급 여부를 확인합니다.
	 * 한 사람당 1번만 발급이 가능하다는 전제 하에 상수는 1로 설정합니다.
	 *
	 * @param couponId  쿠폰 ID
	 * @param memberId  회원 ID
	 * @return 중복 발급 가능 여부
	 */
	public boolean availableUserIssueQuantity(final long couponId, final long memberId) {
		String key = getCouponIssueRequestForDuplicatedCouponKey(couponId, memberId);
		return redisRepository.increment(key) <= AVAILABLE_COUPON_COUNT_PER_USER;
	}
}
