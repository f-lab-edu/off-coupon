package com.flab.offcoupon.service;

import com.flab.offcoupon.domain.vo.persistence.statistics.MonthlyOrderStatisticsVo;
import com.flab.offcoupon.domain.vo.persistence.statistics.MonthlyStatisticsParameterVo;
import com.flab.offcoupon.dto.request.StatisticsRequest;
import com.flab.offcoupon.dto.response.MonthlyOrderStatistics;
import com.flab.offcoupon.repository.mysql.StatisticsRepository;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;

    /**
     * 인덱스를 사용해서 쿼리 최적화를 했지만, where절에 해당하는 레코드가 많을 경우 성능이 떨어질 수 있습니다.<br>
     * 따라서 병렬 스트림을 사용하여 월별로 쿼리를 실행하고 결과를 합치는 방식을 사용했습니다.
     *
     * @param request 월별 주문 통계 조회 요청
     * @return 월별 주문 통계 조회 결과
     */
    @Transactional(readOnly = true)
    public ResponseDTO<List<MonthlyOrderStatistics>> getMonthlyOrderStatistics(StatisticsRequest request) {
        LocalDate startedAt = request.getStartedAt();
        LocalDate endedAt = request.getEndedAt();

        List<MonthlyOrderStatistics> monthlyStatisticsList = new ArrayList<>();

        IntStream.range(0, calculateMonthDiff(startedAt, endedAt) + 1)
                .parallel()
                .forEach(i -> {
                    LocalDate monthStart = startedAt.plusMonths(i);
                    LocalDate monthEnd = monthStart.with(TemporalAdjusters.lastDayOfMonth());
                    MonthlyStatisticsParameterVo parameterVo = new MonthlyStatisticsParameterVo(monthStart, monthEnd);

                    List<MonthlyOrderStatisticsVo> monthlyStatisticsVoList = statisticsRepository.getMonthlyOrderStatistics(parameterVo);
                    List<MonthlyOrderStatistics> monthlyStatistics = convertToDTO(monthlyStatisticsVoList);

                    synchronized (monthlyStatisticsList) {
                        monthlyStatisticsList.addAll(monthlyStatistics);
                    }
                });
        // 월별로 정렬
        monthlyStatisticsList.sort(Comparator.comparing(MonthlyOrderStatistics::getMonth));

        return ResponseDTO.getSuccessResult(monthlyStatisticsList);
    }

    /**
     * 두 날짜 사이의 월 차이를 계산하는 메서드입니다.
     *
     * @param startDate 시작 날짜
     * @param endDate   종료 날짜
     * @return 두 날짜 사이의 월 차이
     */
    private int calculateMonthDiff(LocalDate startDate, LocalDate endDate) {
        // 시작 날짜의 월 첫째 날과 종료 날짜의 월 첫째 날을 기준으로 월 차이를 계산합니다.
        return Period.between(startDate.withDayOfMonth(1), endDate.withDayOfMonth(1)).getYears() * 12
                + Period.between(startDate.withDayOfMonth(1), endDate.withDayOfMonth(1)).getMonths();
    }

    /**
     * MonthlyOrderStatisticsVo 객체를 MonthlyOrderStatistics 객체로 변환하는 메서드입니다.
     *
     * @param monthlyStatisticsVoList 변환할 VO 목록
     * @return 변환된 DTO 목록
     */
    private List<MonthlyOrderStatistics> convertToDTO(List<MonthlyOrderStatisticsVo> monthlyStatisticsVoList) {
        return monthlyStatisticsVoList.stream()
                .map(MonthlyOrderStatistics::new)
                .collect(Collectors.toList());
    }
}
