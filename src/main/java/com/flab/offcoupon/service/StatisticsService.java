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
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class StatisticsService {
    private static final int MIN_MONTHS = 1;
    private static final int MAX_DAYS = 365;
    private static final int INTERVAL = 30;

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
        validateStartDateIsBeforeEndDate(startedAt, endedAt);
        validateDaysBetween(startedAt, endedAt);

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
     * 시작일과 종료일 간의 유효성을 검사하여 시작일이 종료일보다 이전인지 확인합니다.
     *
     * @param startedAt 시작일
     * @param endedAt   종료일
     * @throws IllegalArgumentException 시작일이 종료일보다 이후인 경우 발생하는 예외
     */
    private void validateStartDateIsBeforeEndDate(LocalDate startedAt, LocalDate endedAt) {
         if(startedAt.isAfter(endedAt)) {
             throw new IllegalArgumentException("시작일은 종료일보다 이전이어야 합니다.");
         }
    }
    /**
     * 시작일과 종료일 간의 기간을 검사하여 한 달 이상의 기간인지, 1년 이내의 기간인지,
     * 그리고 한 달 단위로 나누어 떨어지는지 확인합니다.
     *
     * @param startedAt 시작일
     * @param endedAt   종료일
     * @throws IllegalArgumentException 시작일과 종료일이 한 달 이상의 기간이 아니거나, 1년 이상의 기간일 때 발생하는 예외
     * @throws IllegalArgumentException 시작일과 종료일이 한 달 단위로 나누어 떨어지지 않을 때 발생하는 예외
     */
    private void validateDaysBetween(LocalDate startedAt, LocalDate endedAt) {

        // 시작일과 종료일 간의 기간을 계산
        Period period = Period.between(startedAt, endedAt);
        long daysBetween = ChronoUnit.DAYS.between(startedAt, endedAt);

        // 최소 한 달(30일) 이내인지 확인
        if (period.getMonths() + 1 < MIN_MONTHS) {
            throw new IllegalArgumentException("최소 한 달 이상의 기간을 조회해야 합니다.");
        }

        // 최대 1년(365일) 이내인지 확인
        if (period.getDays() > MAX_DAYS) {
            throw new IllegalArgumentException("최대 1년(365일) 이내의 기간을 조회해야 합니다.");
        }

        // 월 단위로 나누어 떨어지지 않는 경우 예외 처리
        if (daysBetween % INTERVAL != 0) {
            throw new IllegalArgumentException("시작일과 종료일을 한 달 단위로 조회해야 합니다.");
        }
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
                .toList();
    }
}
