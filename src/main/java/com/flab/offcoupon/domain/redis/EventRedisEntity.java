package com.flab.offcoupon.domain.redis;

import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.flab.offcoupon.domain.entity.Event;
import com.flab.offcoupon.exception.event.EventPeriodException;
import com.flab.offcoupon.exception.event.EventTimeException;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.flab.offcoupon.exception.event.EventErrorMessage.*;
import static com.flab.offcoupon.exception.event.EventErrorMessage.INVALID_EVENT_TIME;

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
                this (
                        event.getEventId(),
                        event.getCategory(),
                        event.getDescription(),
                        event.getStartDate(),
                        event.getEndDate(),
                        event.getDailyIssueStartTime(),
                        event.getDailyIssueEndTime()
                );
        }

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
