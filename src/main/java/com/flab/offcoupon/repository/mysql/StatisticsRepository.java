package com.flab.offcoupon.repository.mysql;

import com.flab.offcoupon.domain.vo.persistence.statistics.MonthlyOrderStatisticsVo;
import com.flab.offcoupon.domain.vo.persistence.statistics.MonthlyStatisticsParameterVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StatisticsRepository {

    /**
     * 월별 주문 통계를 조회합니다.
     * <p>
     * 다음과 같은 통계를 월별로 조회합니다:
     * <ol>
     *     <li>조회하는 월</li>
     *     <li>주문 수량 총합</li>
     *     <li>주문 총 금액</li>
     *     <li>주문에 사용된 쿠폰 수량 총합</li>
     *     <li>주문에 사용된 쿠폰 총 금액</li>
     * </ol>
     *
     * @param monthlyStatisticsParameterVo 월별 통계 조회 조건
     * @return 월별 주문 통계
     */
    List<MonthlyOrderStatisticsVo> getMonthlyOrderStatistics(final MonthlyStatisticsParameterVo monthlyStatisticsParameterVo);

}
