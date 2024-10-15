package com.flab.offcoupon.util;

import lombok.experimental.UtilityClass;

/**
 * Redis 에서 사용하는 key 를 생성하는 유틸리티 클래스
 */
@UtilityClass
public class RedisKeyUtils {
	private static final String ISSUE_REQUEST_WITH_COUPON_ID_PREFIX = "coupon:issue:request:couponId=%s";
	private static final String COUPON_ISSUE_REQUEST_WITH_COUPON_ID_AND_MEMBER_ID = "coupon:issue:request:couponId=%s:memberId=%s";
	private static final String USER_PUB_SUB_TOPIC_PREFIX_FOR_SSE_CONNECTION = "users:sse:publish:memberId=%s";

	// 누적 쿠폰 발행 개수 제한
	public static String getIssueRequestKey(long couponId) {
		return ISSUE_REQUEST_WITH_COUPON_ID_PREFIX.formatted(couponId);
	}

	// 중복 쿠폰 발행 개수 제한
	public static String getCouponIssueRequestForDuplicatedCouponKey(long couponId, long memberId) {
		return COUPON_ISSUE_REQUEST_WITH_COUPON_ID_AND_MEMBER_ID.formatted(couponId, memberId);
	}

	// sse 연결을 여러 was 서버에서 공유하기 위한 key
	public static String getUserSseConnectionKey() {
		return USER_PUB_SUB_TOPIC_PREFIX_FOR_SSE_CONNECTION;
	}
}
