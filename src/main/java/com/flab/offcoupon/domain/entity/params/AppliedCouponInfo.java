package com.flab.offcoupon.domain.entity.params;

import com.flab.offcoupon.domain.entity.Coupon;
import com.flab.offcoupon.domain.entity.Product;
import com.flab.offcoupon.util.DiscountUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class AppliedCouponInfo {

    private final long couponId;
    private final long discountAmount;

    private AppliedCouponInfo(Product product, Coupon coupon) {
        this.couponId = coupon.getId();
        this.discountAmount = DiscountUtils.calculateDiscountPricePerUnit(product, coupon);
    }

    public static AppliedCouponInfo createAppliedCouponInfo(Product product, Coupon coupon) {
        return new AppliedCouponInfo(product, coupon);
    }
}
