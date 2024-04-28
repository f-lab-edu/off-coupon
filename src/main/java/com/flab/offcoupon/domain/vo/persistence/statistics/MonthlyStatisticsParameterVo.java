package com.flab.offcoupon.domain.vo.persistence.statistics;

import java.time.LocalDate;

public record MonthlyStatisticsParameterVo(
        LocalDate startedAt,
        LocalDate endedAt
) {
}
