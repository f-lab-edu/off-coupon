package com.flab.offcoupon.domain.entity.params;

import com.flab.offcoupon.domain.entity.Coupon;
import com.flab.offcoupon.domain.entity.Product;
import com.flab.offcoupon.util.DiscountUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public final class OrderInfo {
    private final long productId;
    private final long quantity;
    private final long pricePerEach;
    private final long totalOrderPrice;
    private final List<AppliedCouponInfo> appliedCouponInfos;
    private final long totalDiscountPrice;
    private final long totalPaymentPrice;

    private OrderInfo(Product product, long quantity, List<Coupon> couponList) {
        this.productId = product.getId();
        this.quantity = quantity;
        this.pricePerEach = DiscountUtils.pricePerEach(product);
        this.totalOrderPrice = DiscountUtils.totalOrderPrice(product, quantity);
        this.appliedCouponInfos = createAppliedCouponInfos(product, couponList);
        this.totalDiscountPrice = DiscountUtils.totalDiscountPrice(product, couponList, quantity);
        this.totalPaymentPrice = DiscountUtils.totalPaymentPrice(product, quantity, couponList);
    }

    public static OrderInfo createOrderInfo(Product product, long quantity, List<Coupon> couponList) {
        return new OrderInfo(product, quantity, couponList);
    }

    private List<AppliedCouponInfo> createAppliedCouponInfos(Product product, List<Coupon> couponList) {
        return couponList.stream()
                .map(coupon -> AppliedCouponInfo.createAppliedCouponInfo(product, coupon))
                .toList();
    }
}
