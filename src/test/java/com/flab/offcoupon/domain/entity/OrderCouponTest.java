package com.flab.offcoupon.domain.entity;

import com.flab.offcoupon.domain.entity.helper.AppliedCouponInfo;
import com.flab.offcoupon.exception.common.InvalidDiscountAmountException;
import com.flab.offcoupon.exception.common.NonPositiveValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static com.flab.offcoupon.exception.common.InvalidDiscountAmountException.INVALID_DISCOUNT_AMOUNT;
import static com.flab.offcoupon.exception.common.NonPositiveValueException.MUST_BE_POSITIVE;
import static org.junit.jupiter.api.Assertions.*;

class OrderCouponTest {

    @Nested
    @DisplayName("주문에 사용된 쿠폰 정보를 담는 OrderCoupon 생성 테스트")
    class createOrderCoupon {
        @DisplayName("[SUCCESS] Id식별자와 AppliedCouponInfo의 필드가 음수가 아닐 경우 성공적으로 생성된다. AppliedCouponInfo의 discountAmount는 0보다 커야한다.")
        @ParameterizedTest
        @MethodSource("validParams")
        void createOrderCoupon(long couponId, DiscountType discountType, Long discountRate, Long discountPrice) {
            // Given
            Coupon coupon = new Coupon(couponId, 1L, discountType, discountRate, discountPrice, CouponType.FIRST_COME_FIRST_SERVED, 500L, 10L, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
            Product product = new Product(1L, "category", "title", "description", BigDecimal.valueOf(1000), BigDecimal.valueOf(0), BigDecimal.valueOf(1000), null, null);
            AppliedCouponInfo appliedCouponInfo = AppliedCouponInfo.createAppliedCouponInfo(product, coupon);
            // When & Then
            OrderCoupon orderCoupon = OrderCoupon.createOrderCoupon(1L, appliedCouponInfo);
            assertNotNull(orderCoupon);
        }

        private static Stream<Arguments> validParams() {
            return Stream.of(
                    // 쿠폰 ID, 할인 타입, 할인 비율, 할인 가격
                    Arguments.of(1, "PERCENT", 20L, null),
                    Arguments.of(2, "AMOUNT", null, 5000L)
            );
        }

        @DisplayName("[ERROR] Id식별자가 음수이거나 0일 경우 NonPositiveValueException이 발생한다.")
        @ParameterizedTest
        @CsvSource(value = {"-1", "0"})
        void createOrderCoupon2(long couponId) {
            // Given
            Coupon coupon = new Coupon(couponId, 1L, DiscountType.PERCENT, 10L, null, CouponType.FIRST_COME_FIRST_SERVED, 500L, 10L, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
            Product product = new Product(1L, "category", "title", "description", BigDecimal.valueOf(1000), BigDecimal.valueOf(0), BigDecimal.valueOf(1000), null, null);
            // When & Then
            NonPositiveValueException exception = assertThrows(NonPositiveValueException.class, () -> {
                AppliedCouponInfo appliedCouponInfo = AppliedCouponInfo.createAppliedCouponInfo(product, coupon);
                OrderCoupon.createOrderCoupon(1L, appliedCouponInfo);
            });
            assertNotNull(exception);
            assertEquals(MUST_BE_POSITIVE, exception.getMessage());
        }

        @DisplayName("[ERROR] 쿠폰이 적용됐는데, 할인 가격이 음수이거나 0일 경우 InvalidDiscountAmountException이 발생한다.")
        @ParameterizedTest
        @MethodSource("invalidParams")
        void createOrderCoupon3(DiscountType discountType, Long discountRate, Long discountPrice) {
            // Given
            Coupon coupon = new Coupon(1L, 1L, discountType, discountRate, discountPrice, CouponType.FIRST_COME_FIRST_SERVED, 500L, 10L, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
            Product product = new Product(1L, "category", "title", "description", BigDecimal.valueOf(1000), BigDecimal.valueOf(0), BigDecimal.valueOf(1000), null, null);
            // When & Then
            InvalidDiscountAmountException exception = assertThrows(InvalidDiscountAmountException.class, () -> {
                AppliedCouponInfo appliedCouponInfo = AppliedCouponInfo.createAppliedCouponInfo(product, coupon);
                OrderCoupon.createOrderCoupon(1L, appliedCouponInfo);
            });
            assertNotNull(exception);
            assertEquals(INVALID_DISCOUNT_AMOUNT, exception.getMessage());
        }

        private static Stream<Arguments> invalidParams() {
            return Stream.of(
                    // 할인 타입, 할인 비율, 할인 가격
                    Arguments.of("PERCENT", -1L, null),
                    Arguments.of("PERCENT", 0L, null),
                    Arguments.of("AMOUNT", null, -1L),
                    Arguments.of("AMOUNT", null, 0L)
            );
        }
    }
}