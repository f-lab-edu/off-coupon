package com.flab.offcoupon.dto.request;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Generated
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public final class StatisticsRequest {
    private final LocalDate startedAt;
    private final LocalDate endedAt;
}
