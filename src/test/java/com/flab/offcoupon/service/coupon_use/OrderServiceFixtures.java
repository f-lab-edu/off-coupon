package com.flab.offcoupon.service.coupon_use;

import com.flab.offcoupon.domain.entity.CouponStatus;
import com.flab.offcoupon.domain.entity.DiscountType;
import com.flab.offcoupon.domain.vo.persistence.order.AvailableCouponsByMemberIdVo;
import com.flab.offcoupon.domain.vo.persistence.order.CouponIssuesAreActiveVo;
import com.flab.offcoupon.domain.vo.persistence.order.CouponValidationPeriodVo;
import com.flab.offcoupon.dto.request.OrderProductRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class OrderServiceFixtures {

    public static List<AvailableCouponsByMemberIdVo> availableCoupons() {
        List<AvailableCouponsByMemberIdVo> availableCouponData = Arrays.asList(
                new AvailableCouponsByMemberIdVo(1L, LocalDateTime.now(), LocalDateTime.now(), DiscountType.AMOUNT,null, 5000L, "category", "descripton", BigDecimal.valueOf(5000), 1L, CouponStatus.ACTIVE, BigDecimal.valueOf(20000), BigDecimal.valueOf(18000)),
                new AvailableCouponsByMemberIdVo(1L, LocalDateTime.now(), LocalDateTime.now(), DiscountType.AMOUNT,null, 5000L, "category", "descripton", BigDecimal.valueOf(5000), 2L, CouponStatus.ACTIVE, BigDecimal.valueOf(20000), BigDecimal.valueOf(18000))
        );
        return availableCouponData;
    }

    public static List<AvailableCouponsByMemberIdVo> overMinPriceCoupons(BigDecimal originalPrice, BigDecimal minPrice, Long discountPrice) {
        List<AvailableCouponsByMemberIdVo> availableCouponData = Arrays.asList(
                new AvailableCouponsByMemberIdVo(1L, LocalDateTime.now(), LocalDateTime.now(), DiscountType.AMOUNT,null, discountPrice, "category", "descripton", minPrice, 1L, CouponStatus.ACTIVE, originalPrice, BigDecimal.valueOf(0)),
                new AvailableCouponsByMemberIdVo(1L, LocalDateTime.now(), LocalDateTime.now(), DiscountType.PERCENT,50L, null, "category", "descripton", minPrice, 2L, CouponStatus.ACTIVE, originalPrice, BigDecimal.valueOf(0))
        );
        return availableCouponData;
    }

    public static OrderProductRequest orderProductRequest() {
        List<Long> couponIssueId = List.of(1L, 2L);
        List<Long> couponId = List.of(1L);
        long quantity = 1L;
        return new OrderProductRequest(couponIssueId, couponId, quantity);
    }

    public static List<CouponIssuesAreActiveVo> couponIssuesAreActive() {
        CouponIssuesAreActiveVo vo1 = new CouponIssuesAreActiveVo(1L, true);
        CouponIssuesAreActiveVo vo2 = new CouponIssuesAreActiveVo(2L, false);
        return List.of(vo1, vo2);
    }

    public static List<CouponValidationPeriodVo> couponValidationPeriodVo() {
        CouponValidationPeriodVo vo1 = new CouponValidationPeriodVo(1L, LocalDateTime.of(2024,01,01,0,0,0), LocalDateTime.of(2024,01,05,0,0,0));
        CouponValidationPeriodVo vo2 = new CouponValidationPeriodVo(2L, LocalDateTime.of(2024,01,01,0,0,0), LocalDateTime.of(2024,01,03,0,0,0));
        return List.of(vo1, vo2);
    }

    public static List<CouponValidationPeriodVo> couponValidationPeriodVoWithNull() {
        CouponValidationPeriodVo vo1 = new CouponValidationPeriodVo(1L, null, null);
        return List.of(vo1);
    }
}
