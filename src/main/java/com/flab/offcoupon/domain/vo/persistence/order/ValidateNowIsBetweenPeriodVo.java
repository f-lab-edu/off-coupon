package com.flab.offcoupon.domain.vo.persistence.order;

import java.time.LocalDateTime;

/**
 * MyBatis에서 여러개의 반환 값을 전달 받기 위한 VO
 * @param couponId 쿠폰 ID
 * @param isBetweenValidatePeriod 현재 시간이 쿠폰의 유효기간 범위내에 있는지 여부
 */
public record ValidateNowIsBetweenPeriodVo(
        long couponId,
        boolean isBetweenValidatePeriod,
        LocalDateTime validateStartDate,
        LocalDateTime validateEndDate
) {
}
