package com.flab.offcoupon.dto.request;

import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Generated
@Getter
@RequiredArgsConstructor
public final class OrderProductRequest {

        private final List<Long> couponIssueId; // 하나의 주문에 여러개의 쿠폰을 사용할 경우
        private final List<Long> couponId; // 하나의 주문에 여러개의 쿠폰을 사용할 경우
        private final int quantity; // 주문할 상품 수량
}
