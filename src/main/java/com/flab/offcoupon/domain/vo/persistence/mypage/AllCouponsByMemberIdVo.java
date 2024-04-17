package com.flab.offcoupon.domain.vo.persistence.mypage;

import com.flab.offcoupon.domain.entity.CouponStatus;
import com.flab.offcoupon.domain.entity.DiscountType;

import java.time.LocalDateTime;
/**
 * MyBatis에서 여러개의 반환 값을 전달 받기 위한 VO
 *
 * 마이페이지에서 현재 회원이 가진 모든 쿠폰을 조회하기 위해 사용
 */
public record AllCouponsByMemberIdVo(
        long couponId,
        String category,
        String description,
        DiscountType discountType,
        Long discountRate,// NULL 일 경우 AMOUNT
        Long discountPrice,// NULL 일 경우 PERCENT
        LocalDateTime validateStartDate,
        LocalDateTime validateEndDate,
        CouponStatus couponStatus
        )
{}
