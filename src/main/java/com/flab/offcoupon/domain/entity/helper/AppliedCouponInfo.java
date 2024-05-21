package com.flab.offcoupon.domain.entity.helper;

import com.flab.offcoupon.domain.entity.Coupon;
import com.flab.offcoupon.domain.entity.Product;
import com.flab.offcoupon.exception.common.InvalidDiscountAmountException;
import com.flab.offcoupon.model.PositiveLong;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 주문에 적용된 쿠폰 정보를 담는 클래스입니다.
 * 비즈니스 로직에서 데이터 가공을 위해 사용됩니다.
 */
@Getter
public final class AppliedCouponInfo {

    private final long couponId;
    private final BigDecimal discountAmount;

    private AppliedCouponInfo(Product product, Coupon coupon) {
        this.couponId = coupon.getId();
        this.discountAmount = product.calculateDiscountPricePerUnit(coupon);
        validateIdIsNegativeAndDiscountIsNegative(couponId, discountAmount);
    }

    public static AppliedCouponInfo createAppliedCouponInfo(Product product, Coupon coupon) {
        return new AppliedCouponInfo(product, coupon);
    }

    private void validateIdIsNegativeAndDiscountIsNegative(long couponId, BigDecimal discountAmount) {
        PositiveLong positiveLongCouponId = new PositiveLong(couponId);
        if (positiveLongCouponId.getValue() > 0 && (discountAmount.compareTo(BigDecimal.ZERO) < 0 || discountAmount.compareTo(BigDecimal.ZERO) == 0)) {
            throw new InvalidDiscountAmountException();
        }
    }
}
