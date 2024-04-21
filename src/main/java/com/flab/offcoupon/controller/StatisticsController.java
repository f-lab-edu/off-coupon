package com.flab.offcoupon.controller;

import com.flab.offcoupon.dto.request.StatisticsRequest;
import com.flab.offcoupon.dto.response.MonthlyOrderStatistics;
import com.flab.offcoupon.service.StatisticsService;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/statistics")
@RestController
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 1000만건의 데이터를 가정하여, 월별 주문 통계 조회에 대한 쿼리 최적화를 목표로 했습니다
     * <ol>
     *     <li>월별 주문 총 수량</li>
     *     <li>월별 총 주문 금액</li>
     *     <li>월별 주문 중에 사용된 쿠폰 수량</li>
     *     <li>월별 총 쿠폰 할인 금액</li>
     * </ol>
     *
     *
     * @param request
     * @return
     */
    @GetMapping("/monthly-order")
    public ResponseEntity<ResponseDTO<List<MonthlyOrderStatistics>>> getMonthlyOrderStatistics(@RequestBody final StatisticsRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(statisticsService.getMonthlyOrderStatistics(request));
    }
}
