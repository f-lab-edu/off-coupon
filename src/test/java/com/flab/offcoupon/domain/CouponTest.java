package com.flab.offcoupon.domain;

import com.flab.offcoupon.exception.coupon.CouponQuantityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.flab.offcoupon.exception.coupon.ErrorMessage.COUPON_QUANTITY_IS_NULL;
import static com.flab.offcoupon.exception.coupon.ErrorMessage.INVALID_COUPON_QUANTITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CouponTest {

    @Test
    @DisplayName("[ERROR] 쿠폰 발급 수량 확인 - 잔여 수량이 없으면 FALSE 반환")
    void over_issue_coupon_count() {
        Coupon coupon = new Coupon(1, 1,
                DiscountType.PERCENT,
                20L,
                null,
                CouponType.FIRST_COME_FIRST_SERVE,
                100L,
                100L,
                LocalDateTime.of(2024, 03, 01, 00, 0, 0),
                LocalDateTime.of(2024, 03, 31, 00, 0, 0),
                LocalDateTime.now(),
                LocalDateTime.now());
        assertThat(coupon.availableIssueQuantity()).isFalse();
    }
    @Test
    @DisplayName("[SUCCESS] 쿠폰 발급 수량 확인 - 잔여 수량이 남아있으면 TRUE 반환")
    void available_issue_coupon_count() {
        Coupon coupon = new Coupon(1, 1,
                DiscountType.PERCENT,
                20L,
                null,
                CouponType.FIRST_COME_FIRST_SERVE,
                100L,
                99L,
                LocalDateTime.of(2024, 03, 01, 00, 0, 0),
                LocalDateTime.of(2024, 03, 31, 00, 0, 0),
                LocalDateTime.now(),
                LocalDateTime.now());
        assertThat(coupon.availableIssueQuantity()).isTrue();
    }

    @Test
    @DisplayName("[ERROR] 쿠폰 수량 업데이트 - 잔여 수량이 없어서 Exception 발생")
    void issue_coupon_exception() {
        Coupon coupon = new Coupon(1,1,
                DiscountType.PERCENT,
                20L,
                null,
                CouponType.FIRST_COME_FIRST_SERVE,
                100L,
                100L,
                LocalDateTime.of(2024, 03, 01, 00, 0, 0),
                LocalDateTime.of(2024, 03, 31, 00, 0, 0),
                LocalDateTime.now(),
                LocalDateTime.now());
        assertThatThrownBy(() -> coupon.increaseIssuedQuantity(coupon))
                .isInstanceOf(CouponQuantityException.class)
                .hasMessage(INVALID_COUPON_QUANTITY.formatted(100L,100L));
    }

    @Test
    @DisplayName("[ERROR] 쿠폰 수량 업데이트 - 발급 가능 수량이 설정되어있지 않을 경우 Exception 발생")
    void null_coupon_exception() {
        Coupon coupon = new Coupon(1,1,
                DiscountType.PERCENT,
                20L,
                null,
                CouponType.FIRST_COME_FIRST_SERVE,
                null,
                null,
                LocalDateTime.of(2024, 03, 01, 00, 0, 0),
                LocalDateTime.of(2024, 03, 31, 00, 0, 0),
                LocalDateTime.now(),
                LocalDateTime.now());
        assertThatThrownBy(() -> coupon.increaseIssuedQuantity(coupon))
                .isInstanceOf(CouponQuantityException.class)
                .hasMessage(COUPON_QUANTITY_IS_NULL.formatted(null, null));
    }

    @Test
    @DisplayName("[SUCCESS] 쿠폰 수량 업데이트 - 잔여 수량 남아서 수량 업데이트 성공")
    void issue_coupon_increase_quantity() {
        Coupon coupon = new Coupon(1, 1,
                DiscountType.PERCENT,
                20L,
                null,
                CouponType.FIRST_COME_FIRST_SERVE,
                100L,
                99L,
                LocalDateTime.of(2024, 03, 01, 00, 0, 0),
                LocalDateTime.of(2024, 03, 31, 00, 0, 0),
                LocalDateTime.now(),
                LocalDateTime.now());
        // when
        coupon = coupon.increaseIssuedQuantity(coupon);
        // then
        assertEquals(coupon.remainedCoupon(),0L);
    }
}