package com.flab.offcoupon.exception.statistics;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatisticsErrorMessage {
    public static final String START_MUST_BE_BEFORE_THANT_END = "시작일은 종료일보다 이전이어야 합니다. startedAt : %s, endedAt : %s";
    public static final String AT_LEAST_ONE_MONTH_BETWEEN = "최소 한 달 이상의 기간을 조회해야 합니다. startedAt : %s, endedAt : %s";
    public static final String DAYS_BETWEEN_MUST_BE_LESS_THAN_365 = "시작일과 종료일 사이의 일수는 365일 이하여야 합니다. startedAt : %s, endedAt : %s";
    public static final String DAYS_BETWEEN_MUST_BE_ONE_MONTH_BASED = "시작일과 종료일을 한 달 단위로 조회해야 합니다. startedAt : %s, endedAt : %s";
}
