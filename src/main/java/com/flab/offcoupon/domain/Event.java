package com.flab.offcoupon.domain;

import com.flab.offcoupon.exception.event.EventPeriodException;
import com.flab.offcoupon.exception.event.EventTimeException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.flab.offcoupon.exception.event.ErrorMessage.*;

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

    public boolean availableIssueDate(LocalDateTime requestDate) {
        if (startDate == null || endDate == null) {
            throw new EventPeriodException(EVENT_PERIOD_IS_NULL.formatted(startDate, endDate));
        }
        LocalDate currentDate = requestDate.toLocalDate();
        return startDate.isEqual(currentDate) || endDate.isEqual(currentDate) ||
                (startDate.isBefore(currentDate) && endDate.isAfter(currentDate));
    }

    public boolean availableIssueTime(LocalDateTime requestTime) {
        if (dailyIssueStartTime == null || dailyIssueEndTime == null) {
            throw new EventTimeException(EVENT_TIME_IS_NULL.formatted(dailyIssueStartTime, dailyIssueEndTime));
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
            throw new EventPeriodException(INVALID_EVENT_PERIOD.formatted(startDate, endDate));
        }
        if(!availableIssueTime(localDateTime)) {
            throw new EventTimeException(INVALID_EVENT_TIME.formatted(dailyIssueStartTime, dailyIssueEndTime));
        }
    }
}
