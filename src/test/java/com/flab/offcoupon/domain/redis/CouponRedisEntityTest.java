package com.flab.offcoupon.domain.redis;

import com.flab.offcoupon.domain.entity.Coupon;
import com.flab.offcoupon.domain.entity.CouponType;
import com.flab.offcoupon.domain.entity.DiscountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CouponRedisEntityTest {
    private CouponRedisEntity couponRedisEntity;

    @Nested
    @DisplayName("CouponRedisEntity 생성")
    class createCouponRedisEntity {
        @DisplayName("[SUECCESS] CouponRedisEntity 생성")
        @Test
        void createCouponRedisEntity() {

            Coupon coupon = new Coupon(1L, 1L, DiscountType.PERCENT,
                    10L, null, CouponType.FIRST_COME_FIRST_SERVED, 300L, 0L,
                    LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(),LocalDateTime.now());
            couponRedisEntity = new CouponRedisEntity(coupon);
            assertAll(
                    () -> assertNotNull(couponRedisEntity),
                    () -> assertEquals(1L, couponRedisEntity.eventId()),
                    () -> assertEquals(300L, couponRedisEntity.maxQuantity())
            );
        }
    }

}