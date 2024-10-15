package com.flab.offcoupon.service.coupon_issue.async.dto;

import com.flab.offcoupon.domain.redis.CouponRedisEntity;
import com.flab.offcoupon.domain.redis.EventRedisEntity;

public record EventAndCouponCache(
	EventRedisEntity eventCache,
	CouponRedisEntity couponCache
) {
}
