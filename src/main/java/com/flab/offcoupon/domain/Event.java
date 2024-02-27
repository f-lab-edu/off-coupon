package com.flab.offcoupon.domain;

import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
public final class Event {
    @Id
    private final long eventId;
    private final long couponId;
    private final String category;
    private final String description;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String dailyIssueStartTime;
    private final String dailyIssueEndTime;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public boolean availableIssueDate(LocalDateTime requestDate) {
        if (startDate == null || endDate == null) {
            throw new RuntimeException("이벤트 기간이 설정되어있지 않습니다.");
        }
        LocalDate currentDate = requestDate.toLocalDate();
        return startDate.isEqual(currentDate) || endDate.isEqual(currentDate) ||
                (startDate.isBefore(currentDate) && endDate.isAfter(currentDate));
    }

    public boolean availableIssueTime(LocalDateTime requestTime) {
        if (dailyIssueStartTime == null || dailyIssueEndTime == null) {
            throw new RuntimeException("이벤트 시간이 설정되어있지 않습니다.");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime currentTime = requestTime.toLocalTime();

        LocalTime startTime = LocalTime.parse(dailyIssueStartTime, formatter);
        LocalTime endTime = LocalTime.parse(dailyIssueEndTime, formatter);

        return startTime.equals(currentTime) || endTime.equals(currentTime) ||
                (startTime.isBefore(currentTime) && endTime.isAfter(currentTime));
    }

    public void availableIssuePeriodAndTime (LocalDateTime localDateTime) {
        if (!availableIssueDate(localDateTime)) {
            throw new RuntimeException("이벤트 기간이 아닙니다.");
        }
        if(!availableIssueTime(localDateTime)) {
            throw new RuntimeException("이벤트 시간이 아닙니다.");
        }
    }
}
