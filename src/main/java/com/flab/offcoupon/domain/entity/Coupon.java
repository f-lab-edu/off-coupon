package com.flab.offcoupon.domain.entity;

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

    private Coupon(Long eventId, DiscountType discountType, Long discountRate, Long discountPrice, CouponType couponType, Long maxQuantity, Long issuedQuantity, LocalDateTime validateStartDate, LocalDateTime validateEndDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.eventId = eventId;
        this.discountType = discountType;
        this.discountRate = discountRate;
        this.discountPrice = discountPrice;
        this.couponType = couponType;
        this.maxQuantity = maxQuantity;
        this.issuedQuantity = issuedQuantity;
        this.validateStartDate = validateStartDate;
        this.validateEndDate = validateEndDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Coupon create(Long eventId, DiscountType discountType, Long discountRate, Long discountPrice, CouponType couponType, Long maxQuantity, Long issuedQuantity, LocalDateTime validateStartDate, LocalDateTime validateEndDate) {
        LocalDateTime now = LocalDateTime.now();
        return new Coupon(eventId, discountType, discountRate, discountPrice, couponType, maxQuantity, issuedQuantity, validateStartDate, validateEndDate, now, now);
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
