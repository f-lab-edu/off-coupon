package com.flab.offcoupon.domain.redis;

import com.flab.offcoupon.domain.entity.Coupon;
import org.springframework.data.redis.core.RedisHash;

/**
 * Redis에서 캐시로 사용될 쿠폰 엔티티
 * @param id
 * @param eventId
 * @param maxQuantity
 */
@RedisHash("coupon")
public record CouponRedisEntity(
        long id,
        Long eventId, // NULL 일 경우 이벤트와 관련 없는 쿠폰(e.g. 회원가입 쿠폰)
        Long maxQuantity // NULL 일 경우 무제한 발급

) {
    public CouponRedisEntity(Coupon coupon) {
        this(
                coupon.getId(),
                coupon.getEventId(),
                coupon.getMaxQuantity()
        );
    }
}
