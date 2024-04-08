package com.flab.offcoupon.domain.vo.persistence.order;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MyBatis에서 여러개의 파라미터를 전달하기 위한 VO
 *
 * @param couponId
 * @param currentDateTime
 */
public record ValidateCouponPeriodVo (
        List<Long> couponIds,
        LocalDateTime currentDateTime
){
}
