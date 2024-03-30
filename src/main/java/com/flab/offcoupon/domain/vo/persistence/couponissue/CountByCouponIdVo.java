package com.flab.offcoupon.domain.vo.persistence.couponissue;

/**
 * MyBatis에서 여러개의 반환 값을 전달 받기 위한 VO
 * @param couponId
 * @param count
 */
public record CountByCouponIdVo(
        long couponId,
        long count
) {
}
