package com.flab.offcoupon.domain.vo.persistence.statistics;

import java.math.BigDecimal;
import java.time.YearMonth;

/**
 * MyBatis에서 여러개의 반환 값을 전달 받기 위한 VO
 *
 * 월별 주문 통계를 조회하기 위해 사용
 */
public record MonthlyOrderStatisticsVo(
        YearMonth yearMonth,
        long totalOrderCnt,
        BigDecimal totalPaymentPrice,
        long totalCouponUseCnt,
        BigDecimal totalCouponPrice
) {
}
