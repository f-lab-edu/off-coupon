package com.flab.offcoupon.exception.statistics;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatisticsErrorMessage {
    public static final String START_MUST_BE_BEFORE_THANT_END = "시작일은 종료일보다 이전이어야 합니다. startedAt : %s, endedAt : %s";
}
