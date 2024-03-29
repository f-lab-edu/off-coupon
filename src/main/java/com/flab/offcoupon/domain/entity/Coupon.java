package com.flab.offcoupon.domain.entity;

import com.flab.offcoupon.domain.entity.params.CouponParams;
import com.flab.offcoupon.domain.entity.params.DiscountParams;
import com.flab.offcoupon.domain.entity.params.TimeParams;
import com.flab.offcoupon.exception.coupon.CouponQuantityException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.COUPON_QUANTITY_IS_NULL;
import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.INVALID_COUPON_QUANTITY;

@Getter
@ToString
@AllArgsConstructor
public final class Coupon {

    private long id;
    private final Long eventId; // NULL 일 경우 이벤트와 관련 없는 쿠폰(e.g. 회원가입 쿠폰)
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

    private Coupon(Long eventId, DiscountParams discountParams, CouponParams couponParams, TimeParams timeParams) {
        this.eventId = eventId;
        this.discountType = discountParams.discountType();
        this.discountRate = discountParams.discountRate();
        this.discountPrice = discountParams.discountPrice();
        this.couponType = couponParams.couponType();
        this.maxQuantity = couponParams.maxQuantity();
        this.issuedQuantity = couponParams.issuedQuantity();
        this.validateStartDate = couponParams.validateStartDate();
        this.validateEndDate = couponParams.validateEndDate();
        this.createdAt = timeParams.createdAt();
        this.updatedAt = timeParams.updatedAt();
    }

    public static Coupon create(Long eventId, DiscountParams discountParams, CouponParams couponParams) {
        LocalDateTime now = LocalDateTime.now();
        return new Coupon(eventId, discountParams, couponParams, new TimeParams(now, now));
    }

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
