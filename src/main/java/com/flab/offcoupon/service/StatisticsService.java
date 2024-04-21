package com.flab.offcoupon.service;

import com.flab.offcoupon.domain.vo.persistence.statistics.MonthlyOrderStatisticsVo;
import com.flab.offcoupon.domain.vo.persistence.statistics.MonthlyStatisticsParameterVo;
import com.flab.offcoupon.dto.request.StatisticsRequest;
import com.flab.offcoupon.dto.response.MonthlyOrderStatistics;
import com.flab.offcoupon.exception.statistics.LocalDateBadRequestException;
import com.flab.offcoupon.repository.mysql.StatisticsRepository;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static com.flab.offcoupon.exception.statistics.StatisticsErrorMessage.START_MUST_BE_BEFORE_THANT_END;

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
        validateStartDateIsBeforeEndDate(startedAt, endedAt);
        List<MonthlyOrderStatistics> monthlyStatisticsList = new ArrayList<>();
        getMonthlyStatistics(startedAt, endedAt, monthlyStatisticsList);

        // 월별로 정렬
        monthlyStatisticsList.sort(Comparator.comparing(MonthlyOrderStatistics::getMonth));

        return ResponseDTO.getSuccessResult(monthlyStatisticsList);
    }
    /**
     * 시작일과 종료일 간의 유효성을 검사하여 시작일이 종료일보다 이전인지 확인합니다.
     *
     * @param startedAt 시작일
     * @param endedAt   종료일
     * @throws LocalDateBadRequestException 시작일이 종료일보다 이후인 경우 발생하는 예외
     */
    private void validateStartDateIsBeforeEndDate(LocalDate startedAt, LocalDate endedAt) {
        if(startedAt.isAfter(endedAt)) {
            throw new LocalDateBadRequestException(START_MUST_BE_BEFORE_THANT_END.formatted(startedAt, endedAt));
        }
    }
    /**
     * 시작일부터 종료일까지 월 별 주문 통계를 조회하는 메서드입니다.
     *
     * @param startedAt             조회 시작일
     * @param endedAt               조회 종료일
     * @param monthlyStatisticsList 월 별 주문 통계 목록
     */
    private void getMonthlyStatistics(LocalDate startedAt, LocalDate endedAt, List<MonthlyOrderStatistics> monthlyStatisticsList) {
        IntStream.range(startedAt.getMonthValue(), endedAt.getMonthValue() + 1)
                .parallel()
                .forEach(i -> {
                    /**
                     * 시작일부터 종료일까지 월 별로 날짜 설정
                     * ex. 2024년 1월 28일 부터 2024년 3월 10일 까지 조회할 경우
                     * <li>2024년 1월 28일부터 1월 31일</li>
                     * <li>2024년 2월 1일부터 2월 29일</li>
                     * <li>2024년 3월 1일부터 3월 10일</li>
                     */
                    LocalDate starDate = (i == startedAt.getMonthValue()) ? startedAt : LocalDate.of(startedAt.getYear(), i, 1);
                    LocalDate monthEnd = (i == endedAt.getMonthValue()) ? endedAt : getLastDayOfMonth(endedAt.getYear(), i);
                    MonthlyStatisticsParameterVo parameterVo = new MonthlyStatisticsParameterVo(starDate, monthEnd);
                    List<MonthlyOrderStatisticsVo> monthlyStatisticsVoList = statisticsRepository.getMonthlyOrderStatistics(parameterVo);
                    List<MonthlyOrderStatistics> monthlyStatistics = convertToDTO(monthlyStatisticsVoList);
                    monthlyStatisticsList.addAll(monthlyStatistics);
                });
    }

    private LocalDate getLastDayOfMonth(int year, int month) {
        // 연도와 월 정보를 가지고 YearMonth 객체 생성
        YearMonth yearMonth = YearMonth.of(year, month);
        // 해당 월의 마지막 날짜를 반환
        return yearMonth.atEndOfMonth();
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
