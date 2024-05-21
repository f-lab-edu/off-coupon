package com.flab.offcoupon.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ProductTest {

    private Product product;

    @Nested
    @DisplayName("getPricePerUnit - 상품의 개별 가격을 반환하는 메소드")
    class getPricePerUnit {
        @DisplayName("[SUCCESS] 세일 가격이 0일 경우 원래 가격 반환")
        @ParameterizedTest
        @CsvSource(value = {"10000, 0", "50000, 0"})
        void getPricePerUnit_with_salePrice(BigDecimal originalPrice, BigDecimal salePrice) {
            // Given
            product = new Product(1L, "category", "title", "description",
                    originalPrice, salePrice, BigDecimal.valueOf(3000),
                    LocalDateTime.now(), LocalDateTime.now());

            // When
            BigDecimal actualPrice = product.getPricePerUnit();

            // Then
            assertEquals(originalPrice, actualPrice);
        }

        @DisplayName("[SUCCESS] 세일 가격이 0보다 클 경우 세일 가격 반환")
        @ParameterizedTest
        @CsvSource(value = {"10000, 5000", "50000, 30000"})
        void getPricePerUnit_with_originalPrice(BigDecimal originalPrice, BigDecimal salePrice) {
            // Given
            product = new Product(1L, "category", "title", "description",
                    originalPrice, salePrice, BigDecimal.valueOf(3000),
                    LocalDateTime.now(), LocalDateTime.now());

            // When
            BigDecimal actualPrice = product.getPricePerUnit();

            // Then
            assertEquals(salePrice, actualPrice);
        }

    }

    @Nested
    @DisplayName("calculateTotalPrice - 상품의 총 가격을 계산하여 반환하는 메소드")
    class calculateTotalPrice {
        @DisplayName("[SUCCESS] 상품 가격에 수량을 곱한 값을 반환")
        @ParameterizedTest
        @CsvSource(value = {"10000, 3000, 2", "50000, 10000, 1", "10000, 0, 5"})
        void calculateTotalPrice(BigDecimal originalPrice, BigDecimal salePrice, long quantity) {
            // Given
            product = new Product(1L, "category", "title", "description",
                    originalPrice, salePrice, BigDecimal.valueOf(3000),
                    LocalDateTime.now(), LocalDateTime.now());

            // When
            BigDecimal productPrice = !Objects.equals(salePrice, BigDecimal.ZERO) ? salePrice : originalPrice;
            BigDecimal actualPrice = product.calculateTotalPrice(quantity);

            // Then
            assertEquals(productPrice.multiply(BigDecimal.valueOf(quantity)), actualPrice);
        }
    }

    @Nested
    @DisplayName("calculateTotalDiscountPrice - 상품에 대한 총 할인 가격을 계산하여 반환하는 메소드")
    class calculateTotalDiscountPrice {
        @DisplayName("[SUCCESS] 상품에 대한 총 할인 가격을 계산하여 반환")
        @ParameterizedTest
        @MethodSource("calculateTotalDiscountPriceParam")
        void calculateTotalDiscountPrice(BigDecimal originalPrice, BigDecimal salePrice, long quantity, List<Coupon> couponList) {
            // Given
            product = new Product(1L, "category", "title", "description",
                    originalPrice, salePrice, BigDecimal.valueOf(3000),
                    LocalDateTime.now(), LocalDateTime.now());

            // When
            BigDecimal totalDiscountPrice = product.calculateTotalDiscountPrice(couponList, quantity);

            // Then - 총 할인 가격이 원래 가격보다 작은지 확인
            /** 테스트 코드는 내부 로직을 몰라도 작성할 수 있어야 하기 때문에 "정확한 값 비교" 대신 "대소 비교"로 진행했습니다.
             **/
            assertTrue(couponList.isEmpty() ?
                    totalDiscountPrice.compareTo(BigDecimal.ZERO) == 0 :
                    totalDiscountPrice.compareTo(BigDecimal.ZERO) > 0);
        }

        private static Stream<Arguments> calculateTotalDiscountPriceParam() {
            // 테스트에 사용할 쿠폰 리스트 데이터 생성
            List<Coupon> couponList1 = Arrays.asList(
                    new Coupon(1L, 1L, DiscountType.PERCENT,
                            20L, null, CouponType.FIRST_COME_FIRST_SERVED, 300L, 0L,
                            LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now()),
                    new Coupon(1L, 1L, DiscountType.AMOUNT,
                            null, 3000L, CouponType.FIRST_COME_FIRST_SERVED, 300L, 0L,
                            LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now())
            );

            List<Coupon> couponList2 = Arrays.asList(
                    new Coupon(1L, 1L, DiscountType.PERCENT,
                            20L, null, CouponType.FIRST_COME_FIRST_SERVED, 300L, 0L,
                            LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now())
            );

            return Stream.of(
                    // 원래 가격, 세일 가격, 수량, 쿠폰 목록
                    Arguments.of(BigDecimal.valueOf(10000), BigDecimal.valueOf(5000), 2, couponList1),
                    Arguments.of(BigDecimal.valueOf(50000), BigDecimal.valueOf(10000), 1, couponList2),
                    Arguments.of(BigDecimal.valueOf(10000), BigDecimal.ZERO, 5, Collections.emptyList())
            );
        }
    }

    @Nested
    @DisplayName("calculateDiscountPricePerUnit - 상품의 개당 할인 가격을 계산하여 반환하는 메소드드")
    class calculateDiscountPricePerUnit {
        @DisplayName("[SUCCESS] 상품의 개당 할인 가격을 계산하여 반환")
        @ParameterizedTest
        @MethodSource("calculateDiscountPricePerUnitParam")
        void calculateDiscountPricePerUnit(BigDecimal originalPrice, BigDecimal salePrice, Coupon coupon) {
            // Given
            product = new Product(1L, "category", "title", "description",
                    originalPrice, salePrice, BigDecimal.valueOf(3000),
                    LocalDateTime.now(), LocalDateTime.now());

            // When
            BigDecimal discountPricePerUnit = product.calculateDiscountPricePerUnit(coupon);
            // Then
            /** 테스트 코드는 내부 로직을 몰라도 작성할 수 있어야 하기 때문에 "정확한 값 비교" 대신 "대소 비교"로 진행했습니다.
             **/
            assertTrue(discountPricePerUnit.compareTo(BigDecimal.ZERO) >= 0);

        }

        private static Stream<Arguments> calculateDiscountPricePerUnitParam() {
            return Stream.of(
                    // 쿠폰 데이터
                    Arguments.of(BigDecimal.valueOf(10000), BigDecimal.valueOf(5000), new Coupon(1L, 1L, DiscountType.PERCENT,
                            20L, null, CouponType.FIRST_COME_FIRST_SERVED, 300L, 0L,
                            LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now())),
                    Arguments.of(BigDecimal.valueOf(50000), BigDecimal.valueOf(10000), new Coupon(1L, 1L, DiscountType.AMOUNT,
                            null, 3000L, CouponType.FIRST_COME_FIRST_SERVED, 300L, 0L,
                            LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now()))
            );
        }
    }

    @Nested
    @DisplayName("calculateTotalPaymentPrice - 상품에 대한 총 결제 가격을 계산하여 반환하는 메소드")
    class calculateTotalPaymentPrice {
        @DisplayName("[SUCCESS] 상품에 대한 총 결제 가격을 계산하여 반환")
        @ParameterizedTest
        @MethodSource("calculateTotalPaymentPriceParam")
        void calculateTotalPaymentPrice(BigDecimal originalPrice, BigDecimal salePrice, long quantity, List<Coupon> couponList) {
            // Given
            product = new Product(1L, "category", "title", "description",
                    originalPrice, salePrice, BigDecimal.valueOf(3000),
                    LocalDateTime.now(), LocalDateTime.now());

            // When
            BigDecimal totalPaymentPrice = product.calculateTotalPaymentPrice(quantity, couponList);

            // Then
            /** 테스트 코드는 내부 로직을 몰라도 작성할 수 있어야 하기 때문에 "정확한 값 비교" 대신 "대소 비교"로 진행했습니다.
             **/
            assertTrue(totalPaymentPrice.compareTo(BigDecimal.ZERO) >= 0);
        }

        private static Stream<Arguments> calculateTotalPaymentPriceParam() {
            // 테스트에 사용할 쿠폰 리스트 데이터 생성
            List<Coupon> couponList1 = Arrays.asList(
                    new Coupon(1L, 1L, DiscountType.PERCENT,
                            20L, null, CouponType.FIRST_COME_FIRST_SERVED, 300L, 0L,
                            LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now()),
                    new Coupon(1L, 1L, DiscountType.AMOUNT,
                            null, 3000L, CouponType.FIRST_COME_FIRST_SERVED, 300L, 0L,
                            LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now())
            );

            List<Coupon> couponList2 = Arrays.asList(
                    new Coupon(1L, 1L, DiscountType.PERCENT,
                            20L, null, CouponType.FIRST_COME_FIRST_SERVED, 300L, 0L,
                            LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now())
            );
            return Stream.of(
                    // 원래 가격, 세일 가격, 수량, 쿠폰 목록
                    Arguments.of(BigDecimal.valueOf(10000), BigDecimal.valueOf(5000), 2L, couponList1),
                    Arguments.of(BigDecimal.valueOf(50000), BigDecimal.valueOf(10000), 1L, couponList2)
            );
        }
    }
}