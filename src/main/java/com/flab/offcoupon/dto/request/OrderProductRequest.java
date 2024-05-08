package com.flab.offcoupon.dto.request;

import com.flab.offcoupon.model.Positive;
import lombok.Getter;

import java.util.List;

@Getter
public final class OrderProductRequest {

    private final List<Long> couponIssueId; // 하나의 주문에 여러개의 쿠폰을 사용할 경우
    private final List<Long> couponId; // 하나의 주문에 여러개의 쿠폰을 사용할 경우
    private final long quantity; // 주문할 상품 수량

    public OrderProductRequest(final List<Long> couponIssueId, final List<Long> couponId, final long quantity) {
        this.couponIssueId = validateValuesArePositive(couponIssueId);
        this.couponId = validateValuesArePositive(couponId);
        this.quantity = validateValuesIsPositive(quantity);
    }
    private List<Long> validateValuesArePositive(final List<Long> values) {
       return new Positive.Values(values).getValues();
    }
    private long validateValuesIsPositive(final long value) {
        return new Positive(value).getValue();
    }
}
