package com.flab.offcoupon.dto.request;

import com.flab.offcoupon.exception.common.NonPositiveValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class OrderProductRequestTest {

    @Nested
    @DisplayName("OrderProductRequest 생성 테스트")
    class createOrderProductRequest {
        @DisplayName("[SUCCESS] OrderProductRequest 생성 성공")
        @ParameterizedTest
        @MethodSource("onlyPositiveValues")
        void createOrderProductRequest_Success(List<Long> couponIssueId, List<Long> couponId, long quantity) {

            OrderProductRequest orderProductRequest = new OrderProductRequest(couponIssueId, couponId, quantity);

            assertEquals(couponIssueId, orderProductRequest.getCouponIssueId());
            assertEquals(couponId, orderProductRequest.getCouponId());
            assertEquals(quantity, orderProductRequest.getQuantity());

        }

        private static Stream<Arguments> onlyPositiveValues() {
            return Stream.of(
                    arguments(List.of(1L, 2L, 3L), List.of(1L, 2L, 3L), 1L),
                    arguments(List.of(4L, 5L, 6L), List.of(4L, 5L, 6L), 2L),
                    arguments(List.of(7L, 8L, 9L), List.of(7L, 8L, 9L), 3L)
            );
        }

        @DisplayName("[ERROR] OrderProductRequest 객체 생성 실패 시 NonPositiveValueException 발생")
        @ParameterizedTest
        @MethodSource("nonPositiveValues")
        void createOrderProductRequest_Failure(List<Long> couponIssueId, List<Long> couponId, long quantity) {

            assertThrows(NonPositiveValueException.class, () -> {
                new OrderProductRequest(couponIssueId, couponId, quantity);
            });

        }

        private static Stream<Arguments> nonPositiveValues() {
            return Stream.of(
                    arguments(List.of(-1L, 2L, 3L), List.of(1L, 2L, 3L), 1L),
                    arguments(List.of(4L, -5L, -6L), List.of(-4L, -5L, 6L), 2L),
                    arguments(List.of(7L, 8L, 9L), List.of(7L, 8L, 9L), -3L)
            );
        }
    }
}