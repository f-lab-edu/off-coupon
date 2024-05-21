package com.flab.offcoupon.service;

import com.flab.offcoupon.domain.entity.CouponStatus;
import com.flab.offcoupon.domain.entity.DiscountType;
import com.flab.offcoupon.domain.vo.persistence.order.AvailableCouponsByMemberIdVo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class AvailableCouponsByMemberIdVoFixtures {

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
}
