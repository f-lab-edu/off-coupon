package com.flab.offcoupon.service.cache;

import com.flab.offcoupon.domain.redis.CouponRedisEntity;

public interface CouponCacheService {
    CouponRedisEntity getCoupon(long couponId);
}
