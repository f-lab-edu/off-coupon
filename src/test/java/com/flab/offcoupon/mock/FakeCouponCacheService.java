package com.flab.offcoupon.mock;

import com.flab.offcoupon.domain.redis.CouponRedisEntity;
import com.flab.offcoupon.service.cache.CouponCacheService;

public class FakeCouponCacheService implements CouponCacheService {
    @Override
    public CouponRedisEntity getCoupon(long couponId) {
        return null;
    }
}
