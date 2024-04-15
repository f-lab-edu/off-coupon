package com.flab.offcoupon.domain.entity.params;

import com.flab.offcoupon.domain.entity.Coupon;
import com.flab.offcoupon.domain.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public final class AppliedCouponInfo {

    private final long couponId;
    private final BigDecimal discountAmount;

    private AppliedCouponInfo(Product product, Coupon coupon) {
        this.couponId = coupon.getId();
        this.discountAmount = product.calculateDiscountPricePerUnit(coupon);
    }

    public static AppliedCouponInfo createAppliedCouponInfo(Product product, Coupon coupon) {
        return new AppliedCouponInfo(product, coupon);
    }
}
