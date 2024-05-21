package com.flab.offcoupon.service;

import com.flab.offcoupon.domain.vo.persistence.statistics.MonthlyOrderStatisticsVo;
import com.flab.offcoupon.dto.request.StatisticsRequest;
import com.flab.offcoupon.dto.response.MonthlyOrderStatistics;
import com.flab.offcoupon.exception.statistics.LocalDateBadRequestException;
import com.flab.offcoupon.repository.mysql.StatisticsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static com.flab.offcoupon.exception.statistics.StatisticsErrorMessage.START_MUST_BE_BEFORE_THANT_END;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @InjectMocks
    private StatisticsService sut;

    @Mock
    private StatisticsRepository statisticsRepository;

    @DisplayName("[SUCCESS] 월별 주문 통계 조회 테스트")
    @Test
    void getMonthlyOrderStatistics_success() {
        // Given
        LocalDate startDate = LocalDate.of(2022, 1, 1);
        LocalDate endDate = LocalDate.of(2022, 2, 28);
        StatisticsRequest request = new StatisticsRequest(startDate, endDate);

        List<MonthlyOrderStatisticsVo> list = List.of(
                new MonthlyOrderStatisticsVo("202201", 100L, BigDecimal.valueOf(1234), 100L, BigDecimal.valueOf(1234)),
                new MonthlyOrderStatisticsVo("202202", 100L, BigDecimal.valueOf(1234), 100L, BigDecimal.valueOf(1234))
        );

        List<MonthlyOrderStatisticsVo> threadSafeList = new ArrayList<>(list);

        // When
        when(statisticsRepository.getMonthlyOrderStatistics(any())).thenReturn(threadSafeList);
        List<MonthlyOrderStatistics> result = sut.getMonthlyOrderStatistics(request).getData();
        // Then
        assertAll(
                () -> assertEquals(YearMonth.of(2022, 01), result.get(0).getYearMonth()),
                () -> assertEquals(100L, result.get(0).getTotalOrderCnt()),
                () -> assertEquals(BigDecimal.valueOf(1234), result.get(0).getTotalPaymentPrice())
        );
    }

    @DisplayName("[ERROR] 종료일자가 시작일자보다 빠른 경우 LocalDateBadRequestException 발생")
    @Test
    void getMonthlyOrderStatistics_fail_invalid_request() {
        // Given
        LocalDate startDate = LocalDate.of(2022, 4, 1);
        LocalDate endDate = LocalDate.of(2022, 2, 28);
        StatisticsRequest request = new StatisticsRequest(startDate, endDate);
        LocalDateBadRequestException exception = assertThrows(LocalDateBadRequestException.class, () ->
                sut.getMonthlyOrderStatistics(request));
        assertNotNull(exception);
        assertEquals(START_MUST_BE_BEFORE_THANT_END.formatted(startDate, endDate), exception.getMessage());
    }

}