package com.flab.offcoupon.service.coupon_use;

import com.flab.offcoupon.domain.vo.persistence.order.AvailableCouponsByMemberIdVo;
import com.flab.offcoupon.domain.vo.persistence.order.CouponIssuesAreActiveVo;
import com.flab.offcoupon.domain.vo.persistence.order.CouponValidationPeriodVo;
import com.flab.offcoupon.domain.vo.persistence.order.MemberIdProductIdNowVo;
import com.flab.offcoupon.dto.request.OrderProductRequest;
import com.flab.offcoupon.dto.response.AvailableCouponsByMemberIdResponse;
import com.flab.offcoupon.exception.coupon.CouponStatusException;
import com.flab.offcoupon.exception.coupon.CouponUsageInvalidPeriodException;
import com.flab.offcoupon.exception.member.MemberNotFoundException;
import com.flab.offcoupon.exception.product.ProductNotFoundException;
import com.flab.offcoupon.repository.mysql.*;
import com.flab.offcoupon.util.ResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.COUPON_IS_NOT_ACTIVE;
import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.COUPON_USAGE_INVALID_PERIOD;
import static com.flab.offcoupon.exception.member.MemberErrorMessage.NOT_EXIST_MEMBER;
import static com.flab.offcoupon.exception.product.ProductErrorMessage.PRODUCT_NOT_EXIST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @InjectMocks
    private OrderService sut;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CouponIssueRepository couponIssueRepository;
    @Mock
    private CouponRepository couponRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderDetailRepository orderDetailRepository;
    @Mock
    private OrderDetailRepository orderCouponRepository;

    @Nested
    @DisplayName("사용 가능한 쿠폰 목록 조회")
    class getAvailableCoupons {

        List<AvailableCouponsByMemberIdVo> availableCouponData;

        @DisplayName("[SUCCESS] 사용 가능한 쿠폰 목록 조회 성공")
        @Test
        void availableCoupons_return_success() {
            // Given
            long memberId = 1L;
            long productId = 1L;
            LocalDateTime now = LocalDateTime.now();
            availableCouponData = OrderServiceFixtures.availableCoupons();

            when(couponIssueRepository.getAvailableCoupons(new MemberIdProductIdNowVo(memberId, productId, now)))
                    .thenReturn(availableCouponData);

            // When
            ResponseDTO<List<AvailableCouponsByMemberIdResponse>> response = sut.getAvailableCoupons(memberId, productId, now);

            // Then
            assertNotNull(response.getData());
            assertEquals(2, response.getData().size());
        }

        @DisplayName("[SUCCESS] 응답이 appliedDiscountPrice(적용된 할인 가격) 기준으로 내림차순되어 있는지 확인")
        @Test
        void availableCoupons_return_success_descending_based_on_appliedDiscountPrice() {
            // Given
            long memberId = 1L;
            long productId = 1L;
            LocalDateTime now = LocalDateTime.now();
            availableCouponData = OrderServiceFixtures.availableCoupons();

            when(couponIssueRepository.getAvailableCoupons(new MemberIdProductIdNowVo(memberId, productId, now)))
                    .thenReturn(availableCouponData);

            // When
            ResponseDTO<List<AvailableCouponsByMemberIdResponse>> response = sut.getAvailableCoupons(memberId, productId, now);

            // Then
            assertNotNull(response.getData());

            List<AvailableCouponsByMemberIdResponse> responseData = response.getData();
            // 응답이 내림차순으로 정렬되어 있는지 확인하기 위해 stream 사용
            boolean isSortedDescending = IntStream.range(0, responseData.size() - 1)
                    .allMatch(i -> responseData.get(i).getAppliedDiscountPrice()
                            .compareTo(responseData.get(i + 1).getAppliedDiscountPrice()) >= 0);

            assertTrue(isSortedDescending, "appliedDiscountPrice 기준으로 내림차순 되어있습니다.");
        }

        @DisplayName("[SUCCESS] DB에서 조회한 사용 가능한 쿠폰이 없을 경우, 쿠폰 목록이 비어 있어야 합니다.")
        @Test
        void availableCoupons_return_failure_when_no_coupons() {
            // Given
            long memberId = 1L;
            long productId = 1L;
            LocalDateTime now = LocalDateTime.now();

            when(couponIssueRepository.getAvailableCoupons(new MemberIdProductIdNowVo(memberId, productId, now)))
                    .thenReturn(Collections.emptyList());

            // When
            ResponseDTO<List<AvailableCouponsByMemberIdResponse>> response = sut.getAvailableCoupons(memberId, productId, now);

            // Then
            assertTrue(response.getData().isEmpty(), "사용 가능한 쿠폰이 없을 경우 쿠폰 목록이 비어 있어야 합니다.");
        }

        @DisplayName("[SUCCESS] 사용 가능한 쿠폰 목록 필터링이 비즈니스 로직과 일치하는지 확인. " +
                "하나의 상품에 여러 쿠폰이 적용 가능할 경우, 상품 가격에 적용된 할인 가격이 최소 주문 금액 이상인 쿠폰만 반환되어야 합니다.")
        @ParameterizedTest
        @CsvSource(value = {"15000, 8000, 7000", "10000, 8000, 2000", "82000, 80000, 2000", "82000, 80000, 1000"})
        void availableCoupons_return_only_over_minPrice(BigDecimal originalPrice, BigDecimal minPrice, Long discountPrice) {
            // Given
            long memberId = 1L;
            long productId = 1L;
            LocalDateTime now = LocalDateTime.now();
            // 원래 가격이 8만 2천원이고, 최소 주문 금액이 8만원이라면 최대 2천원까지만 할인 가능
            availableCouponData = OrderServiceFixtures.overMinPriceCoupons(originalPrice, minPrice, discountPrice);

            when(couponIssueRepository.getAvailableCoupons(new MemberIdProductIdNowVo(memberId, productId, now)))
                    .thenReturn(availableCouponData);

            // When
            ResponseDTO<List<AvailableCouponsByMemberIdResponse>> response = sut.getAvailableCoupons(memberId, productId, now);
            BigDecimal expectedDiscountPrice = BigDecimal.valueOf(availableCouponData.get(0).discountPrice());
            BigDecimal actualDiscountPrice = response.getData().get(0).getAppliedDiscountPrice();
            // Then
            assertEquals(expectedDiscountPrice, actualDiscountPrice);
        }

        @DisplayName("[SUCCESS] 사용 가능한 쿠폰 목록 필터링이 비즈니스 로직과 일치하는지 확인. " +
                "하나의 상품에 여러 쿠폰이 적용 가능할 경우, DB에서 활성화된 쿠폰 목록 중 최소 금액을 모두 넘는다면 빈 쿠폰 목록이 반환되어야 합니다.")
        @ParameterizedTest
        @CsvSource(value = {"15000, 8000, 10000", "10000, 8000, 10000", "82000, 80000, 5000"})
        void availableCoupons_return_only_over_minPrice_fail(BigDecimal originalPrice, BigDecimal minPrice, Long discountPrice) {
            // Given
            long memberId = 1L;
            long productId = 1L;
            LocalDateTime now = LocalDateTime.now();
            // 원래 가격이 8만 2천원이고, 최소 주문 금액이 8만원이라면 최대 2천원까지만 할인 가능
            availableCouponData = OrderServiceFixtures.overMinPriceCoupons(originalPrice, minPrice, discountPrice);

            when(couponIssueRepository.getAvailableCoupons(new MemberIdProductIdNowVo(memberId, productId, now)))
                    .thenReturn(availableCouponData);

            // When
            ResponseDTO<List<AvailableCouponsByMemberIdResponse>> response = sut.getAvailableCoupons(memberId, productId, now);
            // Then
            assertTrue(response.getData().isEmpty());
        }

        @DisplayName("[ERROR] 유효하지 않은 회원 ID일 경우 MemberNotFoundException 발생")
        @ParameterizedTest
        @CsvSource(value = {"1000", "11234"})
        void availableCoupons_return_failure_when_invalid_memberId(long memberId) {
            // Given
            long productId = 1L;
            LocalDateTime now = LocalDateTime.now();

            when(memberRepository.getMemberById(memberId)).thenThrow(new MemberNotFoundException(NOT_EXIST_MEMBER));

            // When & Then
            MemberNotFoundException exception = assertThrows(MemberNotFoundException.class, ()
                    -> sut.getAvailableCoupons(memberId, productId, now), "유효하지 않은 회원 ID로 예외가 발생해야 합니다.");
            assertNotNull(exception);
            assertEquals(NOT_EXIST_MEMBER, exception.getMessage());
        }

        @DisplayName("[ERROR] 유효하지 않은 상품 ID일 경우 ProductNotFoundException 발생")
        @ParameterizedTest
        @CsvSource(value = {"1000", "11234"})
        void availableCoupons_return_failure_when_invalid_productId(long productId) {
            // Given
            long memberId = 1L;
            LocalDateTime now = LocalDateTime.now();

            when(productRepository.getProductById(productId)).thenThrow(new ProductNotFoundException(PRODUCT_NOT_EXIST.formatted(productId)));

            // When & Then
            ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, ()
                    -> sut.getAvailableCoupons(memberId, productId, now), "유효하지 않은 상품 ID로 예외가 발생해야 합니다.");
            assertNotNull(exception);
            assertEquals(PRODUCT_NOT_EXIST.formatted(productId), exception.getMessage());
        }
    }

    @Nested
    @DisplayName("상품 주문 및 쿠폰 사용 처리")
    class orderProduct {
        OrderProductRequest request;
        @DisplayName("[ERROR] 쿠폰의 상태가 ACTIVE가 아닐 경우 CouponStatusException 발생")
        @Test
        void validateCouponIsAvailable() {
            // Given
            request = OrderServiceFixtures.orderProductRequest();
            LocalDateTime now = LocalDateTime.now();
            List<CouponIssuesAreActiveVo> list = OrderServiceFixtures.couponIssuesAreActive();

            when(couponIssueRepository.validateStatusIsActive(request.getCouponIssueId()))
                    .thenReturn(list);
            // When & Then
            CouponStatusException exception = assertThrows(CouponStatusException.class, ()
                    -> sut.orderProduct(1L, request, now));
            assertNotNull(exception);
            assertEquals(COUPON_IS_NOT_ACTIVE.formatted(List.of(list.get(1).couponIssueId())), exception.getMessage());
        }

        @DisplayName("[ERROR] 현재 시간이 쿠폰의 유효기간 범위 내에 있지 않으면, CouponUsageInvalidPeriodException 발생")
        @Test
        void validateCouponIsAvailable2() {
            // Given
            request = OrderServiceFixtures.orderProductRequest();
            LocalDateTime now = LocalDateTime.now();

            List<CouponValidationPeriodVo> list = OrderServiceFixtures.couponValidationPeriodVo();
            when(couponRepository.getCouponValidationPeriod(request.getCouponId()))
                    .thenReturn(list);
            // When & Then
            CouponUsageInvalidPeriodException exception = assertThrows(CouponUsageInvalidPeriodException.class, ()
                    -> sut.orderProduct(1L, request, now));
            assertNotNull(exception);
            assertTrue(exception.getMessage().startsWith(COUPON_USAGE_INVALID_PERIOD));
        }

        @DisplayName("[ERROR] 쿠폰의 유효기간 범위가 null이면, CouponUsageInvalidPeriodException 발생")
        @Test
        void validateCouponIsAvailable3() {
            // Given
            request = OrderServiceFixtures.orderProductRequest();
            LocalDateTime now = LocalDateTime.now();

            List<CouponValidationPeriodVo> list = OrderServiceFixtures.couponValidationPeriodVoWithNull();
            when(couponRepository.getCouponValidationPeriod(request.getCouponId()))
                    .thenReturn(list);
            // When & Then
            CouponUsageInvalidPeriodException exception = assertThrows(CouponUsageInvalidPeriodException.class, ()
                    -> sut.orderProduct(1L, request, now));
            assertNotNull(exception);
            assertTrue(exception.getMessage().startsWith(COUPON_USAGE_INVALID_PERIOD));
        }
    }

}