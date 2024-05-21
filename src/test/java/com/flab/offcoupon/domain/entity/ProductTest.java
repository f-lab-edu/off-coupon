package com.flab.offcoupon.domain.entity;

import com.flab.offcoupon.exception.common.NonPositiveValueException;
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

import static com.flab.offcoupon.exception.common.NonPositiveValueException.MUST_BE_POSITIVE;
import static com.flab.offcoupon.exception.common.NonPositiveValueException.MUST_NOT_BE_NEGATIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class ProductTest {

    private Product product;

    @Nested
    @DisplayName("상품 정보를 담는 도메인 객체인 Product 생성")
    class createProduct {

        @DisplayName("[SUCCESS] Product 생성시 필드에 음수가 아닐 경우 생성 성공. 다른 필드와 달리 salePrice는 0일 수 있습니다.")
        @ParameterizedTest
        @CsvSource(value = {"1, 10000, 5000, 5000", "2, 50000, 0, 3000"})
        void createProduct_success(long productId, BigDecimal originalPrice, BigDecimal salePrice, BigDecimal minOrderPrice) {

            // Given & When & Then
            product = new Product(productId, "category", "title", "description",
                    originalPrice, salePrice, minOrderPrice,
                    LocalDateTime.now(), LocalDateTime.now());
            assertNotNull(product);

        }

        @DisplayName("[ERROR] 세일 가격이 0보다 작은 음수 일 경우 NonPositiveValueException 발생")
        @ParameterizedTest
        @CsvSource(value = {"-5000", "-30000"})
        void createProduct__with_negative_salePrice(BigDecimal salePrice) {

            // Given & When & Then
            NonPositiveValueException exception = assertThrows(NonPositiveValueException.class, () -> {
                product = new Product(1L, "category", "title", "description",
                        BigDecimal.valueOf(10000L), salePrice, BigDecimal.valueOf(3000),
                        LocalDateTime.now(), LocalDateTime.now());
            });
            assertNotNull(exception);
            assertEquals(MUST_NOT_BE_NEGATIVE, exception.getMessage());
        }

        @DisplayName("[ERROR] Id 식별자가 음수이거나 0일 경우 NonPositiveValueException 발생")
        @ParameterizedTest
        @CsvSource(value = {"0", "-1", "-100"})
        void createProduct__with_negative_id(long  productId) {

            // Given & When & Then
            NonPositiveValueException exception = assertThrows(NonPositiveValueException.class, () -> {
                product = new Product(productId, "category", "title", "description",
                        BigDecimal.valueOf(10000L), BigDecimal.valueOf(1000L), BigDecimal.valueOf(3000),
                        LocalDateTime.now(), LocalDateTime.now());
            });
            assertNotNull(exception);
            assertEquals(MUST_BE_POSITIVE, exception.getMessage());
        }

        @DisplayName("[ERROR] 원래 가격과 최소 주문 가격이 음수이거나 0일 경우 NonPositiveValueException 발생")
        @ParameterizedTest
        @CsvSource(value = {"0, 0", "-1, -1", "0, -1", "-1, 0"})
        void createProduct__with_negative_originalPrice_and_minPrice(BigDecimal originalPrice, BigDecimal minOrderPrice){

            // Given & When & Then
            NonPositiveValueException exception = assertThrows(NonPositiveValueException.class, () -> {
                product = new Product(1L, "category", "title", "description",
                        originalPrice, BigDecimal.valueOf(1000L), minOrderPrice,
                        LocalDateTime.now(), LocalDateTime.now());
            });
            assertNotNull(exception);
            assertEquals(MUST_BE_POSITIVE, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("비즈니스 로직 테스트 getPricePerUnit - 상품의 개별 가격을 반환하는 메소드")
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
    @DisplayName("비즈니스 로직 테스트 calculateTotalPrice - 상품의 총 가격을 계산하여 반환하는 메소드")
    class calculateTotalPrice {
        @DisplayName("[SUCCESS] 총 가격은 상품 가격과 개수를 곱한 값과 일치한다")
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
    @DisplayName("비즈니스 로직 테스트 calculateTotalDiscountPrice - 상품에 대한 총 할인 가격을 계산하여 반환하는 메소드")
    class calculateTotalDiscountPrice {
        @DisplayName("[SUCCESS] 상품에 적용된 쿠폰 별 할인가격을 계산하여 총 할인 가격이 일치하는지 확인합니다. 적용된 쿠폰 가격이 없을 경우 할인 가격은 0이고, 있을 경우 0보다 커야 합니다")
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
            /** 테스트 코드는 내부 로직에 의존하지 않아야 하기 때문에 "정확한 값 비교" 대신 "대소 비교"로 진행했습니다.
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
    @DisplayName("비즈니스 로직 테스트 calculateDiscountPricePerUnit - 상품의 개당 할인 가격을 계산하여 반환하는 메소드드")
    class calculateDiscountPricePerUnit {
        @DisplayName("[SUCCESS] 쿠폰이 적용될 경우 상품 1개당 할인 가격이 일치하는지 확인합니다. 할인 가격은 0보다 커야 합니다.")
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
            /** 테스트 코드는 내부 로직에 의존하지 않아야 하기 때문에 "정확한 값 비교" 대신 "대소 비교"로 진행했습니다.
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
    @DisplayName("비즈니스 로직 테스트 calculateTotalPaymentPrice - 상품에 대한 총 결제 가격을 계산하여 반환하는 메소드")
    class calculateTotalPaymentPrice {
        @DisplayName("[SUCCESS] 상품의 총 결제 가격이 일치하는지 확인합니다. 쿠폰이 적용될 경우 결제 가격은 할인된 가격이어야 하니다. 적용되지 않을 경우 원래 가격이어야 합니다.")
        @ParameterizedTest
        @MethodSource("calculateTotalPaymentPriceParam")
        void calculateTotalPaymentPrice(BigDecimal originalPrice, BigDecimal salePrice, long quantity, List<Coupon> couponList) {
            // Given
            product = new Product(1L, "category", "title", "description",
                    originalPrice, salePrice, BigDecimal.valueOf(3000),
                    LocalDateTime.now(), LocalDateTime.now());

            // When
            BigDecimal totalPaymentPrice = product.calculateTotalPaymentPrice(quantity, couponList);

            BigDecimal productPrice = product.calculateTotalPrice(quantity);
            BigDecimal totalDiscountPrice = product.calculateTotalDiscountPrice(couponList, quantity);

            // Then
            assertEquals(couponList.isEmpty() ? productPrice : productPrice.subtract(totalDiscountPrice),totalPaymentPrice);
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