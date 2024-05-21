package com.flab.offcoupon.domain.entity;

import com.flab.offcoupon.domain.entity.helper.AppliedCouponInfo;
import com.flab.offcoupon.domain.entity.helper.OrderInfo;
import com.flab.offcoupon.exception.common.InvalidProductPriceException;
import com.flab.offcoupon.exception.common.NonPositiveValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.flab.offcoupon.exception.common.InvalidProductPriceException.INVALID_PRODUCT_PRICE;
import static com.flab.offcoupon.exception.common.NonPositiveValueException.MUST_BE_POSITIVE;
import static org.junit.jupiter.api.Assertions.*;

class OrderDetailTest {
    /**
     * 이 테스트는 주문 정보를 담는 DTO인 OrderInfo를 이용하여 <br>
     * 비즈니스 로직에 따라 도메인 객체인 OrderDetail을 생성합니다.<br>
     * <ul>추가 정보
     * <li> AppliedCouponInfo의 경우 사용된 쿠폰이 없는 주문일 경우 Null일 수 있습니다. </li>
     * <li> AppliedCouponInfo의 경우 쿠폰이 사용된 주문일 경우 Null일 수 없습니다. </li>
     * </ul>
     */
    @Nested
    @DisplayName("주문 상세 정보를 담는 도메인 객체인 OrderDetail 생성")
    class createOrderDetail {
        @DisplayName("[SUCCESS] Id 식별자와 상품 1개당 가격이 음수가 아닐 경우 성공적으로 생성된다. 상품 1개당 가격(pricePerEach)은 0보다 커야한다.")
        @ParameterizedTest
        @MethodSource("validParams")
        void createOrderDetail_success(long productId, long quantity, BigDecimal pricePerEach, List<AppliedCouponInfo> appliedCouponInfos) {
            // Given
            OrderInfo orderInfo = new OrderInfo(productId, quantity, pricePerEach, BigDecimal.valueOf(10000L),
                    appliedCouponInfos, BigDecimal.valueOf(3000), BigDecimal.valueOf(7000));
            OrderDetail orderCoupon = OrderDetail.createOrderDetail(orderInfo);
            assertNotNull(orderCoupon);
        }

        private static Stream<Arguments> validParams() {
            List<AppliedCouponInfo> appliedCouponInfos = new ArrayList<>();
            Coupon coupon = new Coupon(1L, 1L, DiscountType.PERCENT, 20L, null, CouponType.FIRST_COME_FIRST_SERVED, 500L, 10L, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
            Product product = new Product(1L, "category", "title", "description", BigDecimal.valueOf(1000), BigDecimal.valueOf(0), BigDecimal.valueOf(1000), null, null);
            AppliedCouponInfo appliedCouponInfo = AppliedCouponInfo.createAppliedCouponInfo(product, coupon);
            appliedCouponInfos.add(appliedCouponInfo);

            return Stream.of(
                    // 상품 ID, 수량, 상품 1개당 가격, 적용된 쿠폰 정보
                    Arguments.of(1L, 20L, BigDecimal.valueOf(3000L), null),
                    Arguments.of(2L, 1L, BigDecimal.valueOf(10000L), appliedCouponInfos)
            );
        }

        @DisplayName("[ERROR] Id식별자와 quantity가 음수이거나 0일 경우 NonPositiveValueException이 발생한다.")
        @ParameterizedTest
        @CsvSource(value = {"-1, 0", "0, -1", "-1, -1", "0, 0"})
        void createOrderDetail_fail_with_invalid_Id_and_quantity(long productId, long quantity) {
            // Given
            OrderInfo orderInfo = new OrderInfo(productId, quantity, BigDecimal.valueOf(20000L), BigDecimal.valueOf(10000L),
                    null, BigDecimal.valueOf(3000), BigDecimal.valueOf(7000));
            // When & Then
            NonPositiveValueException exception = assertThrows(NonPositiveValueException.class, () -> {
                OrderDetail.createOrderDetail(orderInfo);
            });
            assertNotNull(exception);
            assertEquals(MUST_BE_POSITIVE, exception.getMessage());
        }

        @DisplayName("[ERROR] 상품 1개당 가격이 음수이거나 0일 경우 InvalidProductPriceException이 발생한다.")
        @ParameterizedTest
        @CsvSource(value = {"-1, 0, -1000"})
        void createOrderDetail_fail_with_invalid_pricePerEach(BigDecimal pricePerEach) {
            // Given
            OrderInfo orderInfo = new OrderInfo(1L, 2L,pricePerEach, BigDecimal.valueOf(10000L),
                    null, BigDecimal.valueOf(3000), BigDecimal.valueOf(7000));
            // When & Then
            InvalidProductPriceException exception = assertThrows(InvalidProductPriceException.class, () -> {
                OrderDetail.createOrderDetail(orderInfo);
            });
            assertNotNull(exception);
            assertEquals(INVALID_PRODUCT_PRICE, exception.getMessage());
        }
    }
}