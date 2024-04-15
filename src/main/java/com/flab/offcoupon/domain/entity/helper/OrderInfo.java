package com.flab.offcoupon.domain.entity.helper;

import com.flab.offcoupon.domain.entity.Coupon;
import com.flab.offcoupon.domain.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 주문 정보를 담는 클래스입니다.
 * 비즈니스 로직에서 데이터 가공을 위해 사용됩니다.
 */
@Getter
@AllArgsConstructor
public final class OrderInfo {
    private final long productId;
    private final long quantity;
    private final BigDecimal pricePerEach;
    private final BigDecimal totalOrderPrice;
    private final List<AppliedCouponInfo> appliedCouponInfos;
    private final BigDecimal totalDiscountPrice;
    private final BigDecimal totalPaymentPrice;

    private OrderInfo(Product product, long quantity, List<Coupon> couponList) {
        this.productId = product.getId();
        this.quantity = quantity;
        this.pricePerEach = product.getPricePerUnit();
        this.totalOrderPrice = product.calculateTotalPrice(quantity);
        this.appliedCouponInfos = createAppliedCouponInfos(product, couponList);
        this.totalDiscountPrice = product.calculateTotalDiscountPrice(couponList, quantity);
        this.totalPaymentPrice = product.calculateTotalPaymentPrice(quantity, couponList);
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
