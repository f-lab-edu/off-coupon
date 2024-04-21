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
     * 1000만의 데이터로 월별 주문 통계 조회에 대한 쿼리 최적화와 속도 개선을 목표로 했습니다
     * 관련된 내용을 포스팅하여 블로그에 작성했으며, 링크는 아래에 있는 @See에서 확인할 수 있습니다.
     * <p>
     * 1. DB 쿼리 속도 개선
     * <ul>
     *     <li> 테이블 당 1000만건의 데이터가 있다고 가정하고, csv파일로 로컬 DB에 import했습니다.</li>
     *     <li> EXPLAIN명령어를 사용하여 실행계획을 분석하고, 복합인덱스, 커버링인덱스를 사용하여 약 2배의 속도를 개선했습니다.</li>
     * </ul>
     * </p>
     * <p>
     *     2. 애플리케이션 속도 개선
     *     <ul>
     *         <li> 쿼리를 최적화여 속도는 개선했지만, where절 기준으로 여전히 읽어야할 레코드의 수가 많아서 만족스러운 속도가 나오지 않았습니다.</li>
     *         <li> 따라서 요청의 시작일과 종료일을 1달 기준으로 분리하여 쿼리를 날렸지만, 결국에 월별로 실행된 쿼리도 1s씩 합쳐지게 되어 결국 속도가 다를바 없었습니다. </li>
     *         <li> 병렬 스트림을 사용하여 월별로 쿼리를 병렬적으로 수행하여 약 3배의 속도를 개선했습니다</li>
     *     </ul>
     * </p>
     * @param request 월별 주문 통계 조회 요청
     * @return 월별 주문 통계 조회 결과
     * @See <a href="https://strong-park.tistory.com/entry/1000%EB%A7%8C%EA%B1%B4%EC%9D%98-%EB%8D%B0%EC%9D%B4%ED%84%B0%EB%A5%BC-%EB%8C%80%EC%83%81%EC%9C%BC%EB%A1%9C-%EC%BF%BC%EB%A6%AC%EC%B5%9C%EC%A0%81%ED%99%94%EB%A5%BC-%EC%A0%81%EC%9A%A9%ED%95%B4%EB%B3%B4%EC%9E%90">1000만건의 데이터를 대상으로 쿼리최적화 with. 복합인덱스, 커버링인덱스</a>
     */
    @GetMapping("/monthly-order")
    public ResponseEntity<ResponseDTO<List<MonthlyOrderStatistics>>> getMonthlyOrderStatistics(@RequestBody final StatisticsRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(statisticsService.getMonthlyOrderStatistics(request));
    }
}
