package com.flab.offcoupon.domain;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public final class Coupon {

    private final long id;
    private final DiscountType discountType;
    private final Long discountRate; // NULL 일 경우 AMOUNT
    private final Long discountPrice; // NULL 일 경우 PERCENT
    private final CouponType couponType;
    private final Long maxQuantity; // NULL 일 경우 무제한 발급
    private final Long issuedQuantity; // NULL 일 경우 무제한 발급
    private final LocalDateTime validateStartDate;
    private final LocalDateTime validateEndDate;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public boolean availableIssueQuantity() {
        if (maxQuantity == null || issuedQuantity == null) {
            throw new RuntimeException("쿠폰의 발급 가능 수량이 설정되어있지 않습니다.");
        }
        return maxQuantity > issuedQuantity;
    }

    public Coupon increaseIssuedQuantity(Coupon originalCoupon) {
        if (!originalCoupon.availableIssueQuantity()) {
            throw new RuntimeException("발급 가능한 쿠폰이 없습니다.");
        }
        return new Coupon(
                originalCoupon.id,
                originalCoupon.discountType,
                originalCoupon.discountRate,
                originalCoupon.discountPrice,
                originalCoupon.couponType,
                originalCoupon.maxQuantity,
                originalCoupon.issuedQuantity + 1,
                originalCoupon.validateStartDate,
                originalCoupon.validateEndDate,
                originalCoupon.createdAt,
                originalCoupon.updatedAt
        );
    }

    public Long remainedCoupon() {
        return maxQuantity - issuedQuantity;
    }
}
