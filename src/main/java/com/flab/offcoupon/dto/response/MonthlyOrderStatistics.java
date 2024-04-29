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
        this.yearMonth = yearMonthFormatter(vo.yearMonth());
        this.totalOrderCnt = vo.totalOrderCnt();
        this.totalPaymentPrice = vo.totalPaymentPrice();
        this.totalCouponUseCnt = vo.totalCouponUseCnt();
        this.totalCouponPrice = vo.totalCouponPrice();
    }

    private YearMonth yearMonthFormatter(String yearMonth) {
        return YearMonth.parse(yearMonth.substring(0,4)+ "-" + yearMonth.substring(4));
    }
}
