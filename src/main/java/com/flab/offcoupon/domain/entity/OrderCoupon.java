package com.flab.offcoupon.domain.entity;

import com.flab.offcoupon.domain.entity.params.AppliedCouponInfo;
import com.flab.offcoupon.domain.entity.params.TimeParams;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 주문에 사용된 쿠폰 정보를 담는 도메인 객체입니다.
 * 하나의 주문에 여러 쿠폰을 사용할 수 있기 때문에 별도의 테이블로 분리하였습니다.
 */
@ToString
@Getter
@AllArgsConstructor
public final class OrderCoupon {
    private long id;
    private final long orderDetailId;
    private final long couponId;
    private final BigDecimal discountAmount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private OrderCoupon(long orderDetailId, AppliedCouponInfo coupon, TimeParams timeParams){
        this.orderDetailId = orderDetailId;
        this.couponId = coupon.getCouponId();
        this.discountAmount = coupon.getDiscountAmount();
        this.createdAt = timeParams.createdAt();
        this.updatedAt = timeParams.updatedAt();
    }

    public static OrderCoupon createOrderCoupon(long orderDetailId, AppliedCouponInfo coupon) {
        LocalDateTime now = LocalDateTime.now();
        return new OrderCoupon(orderDetailId, coupon, new TimeParams(now, now));
    }
}