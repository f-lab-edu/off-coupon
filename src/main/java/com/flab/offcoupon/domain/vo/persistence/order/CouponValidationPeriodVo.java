package com.flab.offcoupon.domain.vo.persistence.order;

import java.time.LocalDateTime;

/**
 * MyBatis에서 여러개의 반환 값을 전달 받기 위한 VO
 *
 * @param couponId 쿠폰 ID
 * @Param validateStartDate 쿠폰 유효기간 시작일
 * @Param validateEndDate 쿠폰 유효기간 종료일
 */
public record CouponValidationPeriodVo(
        long couponId,
        LocalDateTime validateStartDate,
        LocalDateTime validateEndDate
) {
}
