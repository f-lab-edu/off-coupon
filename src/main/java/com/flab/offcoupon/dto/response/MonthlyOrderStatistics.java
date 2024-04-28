package com.flab.offcoupon.dto.response;

import com.flab.offcoupon.domain.vo.persistence.statistics.MonthlyOrderStatisticsVo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.YearMonth;


@Getter
@AllArgsConstructor
public final class MonthlyOrderStatistics {

    private final YearMonth yearMonth;
    private final long totalOrderCnt;
    private final BigDecimal totalPaymentPrice;
    private final long totalCouponUseCnt;
    private final BigDecimal totalCouponPrice;

    public MonthlyOrderStatistics(MonthlyOrderStatisticsVo vo) {
        this.yearMonth = vo.yearMonth();
        this.totalOrderCnt = vo.totalOrderCnt();
        this.totalPaymentPrice = vo.totalPaymentPrice();
        this.totalCouponUseCnt = vo.totalCouponUseCnt();
        this.totalCouponPrice = vo.totalCouponPrice();
    }
}
