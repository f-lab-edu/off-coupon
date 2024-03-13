package com.flab.offcoupon.domain.entity;

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

    private Event(String category, String description, LocalDate startDate, LocalDate endDate, String dailyIssueStartTime, String dailyIssueEndTime, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.category = category;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dailyIssueStartTime = dailyIssueStartTime;
        this.dailyIssueEndTime = dailyIssueEndTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Event create(String category, String description, LocalDate startDate, LocalDate endDate, String dailyIssueStartTime, String dailyIssueEndTime) {
        LocalDateTime now = DateTimeUtils.nowFromZone();
        return new Event(category, description, startDate, endDate, dailyIssueStartTime, dailyIssueEndTime, now, now);
    }
}
