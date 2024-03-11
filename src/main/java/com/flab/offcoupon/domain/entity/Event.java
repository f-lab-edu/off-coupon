package com.flab.offcoupon.domain.entity;

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
    private long eventId;
    private final String category;
    private final String description;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String dailyIssueStartTime;
    private final String dailyIssueEndTime;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
