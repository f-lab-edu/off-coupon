package com.flab.offcoupon.dto.request;

import lombok.*;

import java.time.LocalDate;

@Generated
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public final class StatisticsRequest {
    @NonNull
    private final LocalDate startedAt;
    @NonNull
    private final LocalDate endedAt;
}
