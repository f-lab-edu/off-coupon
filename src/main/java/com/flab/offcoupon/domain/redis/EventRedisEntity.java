package com.flab.offcoupon.domain.redis;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.flab.offcoupon.domain.entity.Event;
import com.flab.offcoupon.exception.event.EventPeriodException;
import com.flab.offcoupon.exception.event.EventTimeException;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.*;
import java.time.format.DateTimeFormatter;

import static com.flab.offcoupon.exception.event.EventErrorMessage.*;

/**
 * Redis에서 캐시로 사용될 이벤트 엔티티
 * @param eventId
 * @param category
 * @param description
 * @param startDate
 * @param endDate
 * @param dailyIssueStartTime
 * @param dailyIssueEndTime
 */
@RedisHash("event")
public record EventRedisEntity(
        @Id
        long eventId,

        String category,

        String description,

        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateDeserializer.class)
        LocalDate startDate,

        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateDeserializer.class)
        LocalDate endDate,
        String dailyIssueStartTime,
        String dailyIssueEndTime
) {

    public EventRedisEntity(Event event) {
        this(
                event.getEventId(),
                event.getCategory(),
                event.getDescription(),
                event.getStartDate(),
                event.getEndDate(),
                event.getDailyIssueStartTime(),
                event.getDailyIssueEndTime()
        );
    }

    /**
     * 이벤트 발급 기간 검증
     *
     * @param requestDate
     * @return boolean
     * @throws EventPeriodException 이벤트 기간이 아닐 경우(NULL체크)
     */
    public boolean availableIssueDate(LocalDateTime requestDate) {
        if (startDate == null || endDate == null) {
            throw new EventPeriodException(EVENT_PERIOD_IS_NULL.formatted(startDate, endDate));
        }
        LocalDate currentDate = requestDate.toLocalDate();

        /**
         * 요청 날짜가 startDate와 endDate 사이에 있는지 확인
         * Period.between(a,b) : a와 b 사이의 기간을 반환, a가 b보다 날짜상으로 이전이면 양수, 이후면 음수
         */
        Period periodFromStart = Period.between(startDate, currentDate);
        Period periodFromEnd = Period.between(currentDate, endDate);

        return (periodFromStart.isZero() || !periodFromStart.isNegative()) &&
                (periodFromEnd.isZero() || !periodFromEnd.isNegative());
    }

    /**
     * 이벤트 발급 시간 검증
     *
     * @param requestTime
     * @return boolean
     * @throws EventTimeException 이벤트 시간이 아닐 경우(NULL체크)
     */
    public boolean availableIssueTime(LocalDateTime requestTime) {
        if (dailyIssueStartTime == null || dailyIssueEndTime == null) {
            throw new EventTimeException(EVENT_TIME_IS_NULL.formatted(dailyIssueStartTime, dailyIssueEndTime));
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        LocalTime startTime = LocalTime.parse(dailyIssueStartTime, formatter);
        LocalTime endTime = LocalTime.parse(dailyIssueEndTime, formatter);

        /**
         * 요청 시간이 dailyIssueStartTime과 dailyIssueEndTime 사이에 있는지 확인
         * Duration.between(a,b) : a와 b 사이의 시간을 반환, a가 b보다 시간상으로 이전이면 양수, 이후면 음수
         */
        Duration startDuration = Duration.between(startTime, requestTime.toLocalTime());
        Duration endDuration = Duration.between(requestTime.toLocalTime(), endTime);

        return (startDuration.isZero() || !startDuration.isNegative()) &&
                (endDuration.isZero() || !endDuration.isNegative());
    }

    /**
     * 이벤트 발급 기간 및 시간 검증
     *
     * @param localDateTime
     * @throws EventPeriodException 이벤트 기간이 아닐 경우
     * @throws EventTimeException 이벤트 시간이 아닐 경우
     */
    public void availableIssuePeriodAndTime (LocalDateTime localDateTime) {
        if (!availableIssueDate(localDateTime)) {
            throw new EventPeriodException(INVALID_EVENT_PERIOD.formatted(startDate, endDate));
        }
        if(!availableIssueTime(localDateTime)) {
            throw new EventTimeException(INVALID_EVENT_TIME.formatted(dailyIssueStartTime, dailyIssueEndTime));
        }
    }
}
