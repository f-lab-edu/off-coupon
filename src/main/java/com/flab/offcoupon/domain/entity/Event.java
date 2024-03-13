package com.flab.offcoupon.domain.entity;

import com.flab.offcoupon.domain.entity.params.EventPeriodAndTimeParams;
import com.flab.offcoupon.domain.entity.params.TimeParams;
import com.flab.offcoupon.util.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
public final class Event {
    @Id
    private long id;
    private final String category;
    private final String description;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String dailyIssueStartTime;
    private final String dailyIssueEndTime;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private Event(String category, String description, EventPeriodAndTimeParams eventPeriodAndTimeParams, TimeParams timeParams) {
        this.category = category;
        this.description = description;
        this.startDate = eventPeriodAndTimeParams.startDate();
        this.endDate = eventPeriodAndTimeParams.endDate();
        this.dailyIssueStartTime = eventPeriodAndTimeParams.dailyIssueStartTime();
        this.dailyIssueEndTime = eventPeriodAndTimeParams.dailyIssueEndTime();
        this.createdAt = timeParams.createdAt();
        this.updatedAt = timeParams.updatedAt();
    }

    public static Event create(String category, String description,EventPeriodAndTimeParams eventPeriodAndTimeParams) {
        LocalDateTime now = DateTimeUtils.nowFromZone();
        return new Event(category, description, eventPeriodAndTimeParams, new TimeParams(now, now));
    }
}
