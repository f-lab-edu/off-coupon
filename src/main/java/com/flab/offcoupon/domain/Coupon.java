package com.flab.offcoupon.domain;

import com.flab.offcoupon.exception.coupon.CouponQuantityException;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

import static com.flab.offcoupon.exception.coupon.ErrorMessage.COUPON_QUANTITY_IS_NULL;
import static com.flab.offcoupon.exception.coupon.ErrorMessage.INVALID_COUPON_QUANTITY;

@ToString
@AllArgsConstructor
public final class Coupon {

    private long id;
    private final long eventId;
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
            throw new CouponQuantityException(COUPON_QUANTITY_IS_NULL.formatted(maxQuantity, issuedQuantity));
        }
        return maxQuantity > issuedQuantity;
    }

    public Coupon increaseIssuedQuantity(Coupon originalCoupon) {
        if (!originalCoupon.availableIssueQuantity()) {
            throw new CouponQuantityException(INVALID_COUPON_QUANTITY.formatted(maxQuantity, issuedQuantity));
        }
        return new Coupon(
                originalCoupon.id,
                originalCoupon.eventId,
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
