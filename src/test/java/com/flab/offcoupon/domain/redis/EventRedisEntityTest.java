package com.flab.offcoupon.domain.redis;

import com.flab.offcoupon.domain.entity.Event;
import com.flab.offcoupon.exception.event.EventPeriodException;
import com.flab.offcoupon.exception.event.EventTimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.flab.offcoupon.exception.event.EventErrorMessage.*;
import static org.junit.jupiter.api.Assertions.*;

class EventRedisEntityTest {
    private LocalDate startDate = LocalDate.of(2024, 02, 01);
    private LocalDate endDate = startDate.plusDays(10);
    private String dailyIssueStartTime = "13:00:00";
    private String dailyIssueEndTime = "15:00:00";
    private EventRedisEntity eventRedisEntity;

    @Nested
    @DisplayName("EventRedisEntity 생성")
    class createEventRedisEntity {
        @DisplayName("[SUECCESS] EventRedisEntity 생성")
        @Test
        void createEventRedisEntity() {

            Event event = new Event(1L, "category", "description",
                    startDate, endDate, dailyIssueStartTime, dailyIssueEndTime, LocalDateTime.now(), LocalDateTime.now());
            eventRedisEntity = new EventRedisEntity(event);

            assertNotNull(eventRedisEntity);
            assertEquals(1L, eventRedisEntity.eventId());
            assertEquals("category", eventRedisEntity.category());
            assertEquals("description", eventRedisEntity.description());
            assertEquals(startDate, eventRedisEntity.startDate());
            assertEquals(endDate, eventRedisEntity.endDate());
            assertEquals(dailyIssueStartTime, eventRedisEntity.dailyIssueStartTime());
            assertEquals(dailyIssueEndTime, eventRedisEntity.dailyIssueEndTime());
        }
    }

    @Nested
    @DisplayName("availableIssuePeriodAndTime - 이벤트 기간 검증")
    class availableIssuePeriodAndTimeMethodForEventPeriod {
        LocalDateTime validNow =  LocalDateTime.of(2024, 02, 01, 13, 00, 00);

        @DisplayName("[ERROR] 요청 값이 이벤트 기간 범위가 아닌 경우 EventPeriodException 발생")
        @ParameterizedTest(name = "[{index}] {1}")
        @CsvSource({
                "2024-01-01T13:00:00, 요청 값이 이벤트 기간 범위보다 이전 일 경우",
                "2024-03-01T13:00:00, 요청 값이 이벤트 기간 범위보다 이후 일 경우"
        })
        void availableIssuePeriodAndTime_with_fail_invalid_request_date(String requestDateString, String caseDescription) {
            LocalDateTime requestDate = LocalDateTime.parse(requestDateString);

            eventRedisEntity = new EventRedisEntity(1L, "category", "description",
                    startDate, endDate, dailyIssueStartTime, dailyIssueEndTime);

            EventPeriodException exception = assertThrows(EventPeriodException.class, () -> eventRedisEntity.availableIssuePeriodAndTime(requestDate));

            assertNotNull(exception);
            assertEquals(INVALID_EVENT_PERIOD.formatted(startDate, endDate), exception.getMessage());
        }
        @DisplayName("[ERROR] 설정된 이벤트 기간이 null 경우 EventPeriodException 발생 ")
        @Test
        void availableIssuePeriodAndTime_fail_with_null() {
            eventRedisEntity = new EventRedisEntity(1L, "category", "description",
                    null, null, "00:00:00", "23:59:59");

            EventPeriodException exception = assertThrows(EventPeriodException.class, () -> eventRedisEntity.availableIssuePeriodAndTime(validNow));
            assertNotNull(exception);
            assertEquals(EVENT_PERIOD_IS_NULL.formatted(null, null), exception.getMessage());
        }

        @DisplayName("[SUCCESS] 요청 값이 이벤트 기간 범위인 경우 ")
        @Test
        void availableIssuePeriodAndTime_success() {
            eventRedisEntity = new EventRedisEntity(1L, "category", "description",
                    startDate, endDate, dailyIssueStartTime, dailyIssueEndTime);
            assertDoesNotThrow(() -> eventRedisEntity.availableIssuePeriodAndTime(validNow));
        }
    }

    @Nested
    @DisplayName("availableIssuePeriodAndTime - 이벤트 시간 검증")
    class availableIssuePeriodAndTimeMethodForEventTime {
        LocalDateTime validNow =  LocalDateTime.of(2024, 02, 01, 13, 00, 00);

        @DisplayName("[ERROR] 요청 값이 이벤트 시간 범위가 아닌 경우 EventTimeException 발생 ")
        @ParameterizedTest(name = "[{index}] {1}")
        @CsvSource({
                "2024-02-01T08:00:00, 요청 값이 이벤트 시간 범위보다 이전 일 경우",
                "2024-02-01T20:00:00, 요청 값이 이벤트 시간 범위보다 이후 일 경우"
        })
        void availableIssuePeriodAndTime_with_invalid_request_time(String requestDateString, String caseDescription) {
            LocalDateTime requestDate = LocalDateTime.parse(requestDateString);

            eventRedisEntity = new EventRedisEntity(1L, "category", "description",
                    startDate, endDate,  dailyIssueStartTime, dailyIssueEndTime);

            EventTimeException exception = assertThrows(EventTimeException.class, () -> eventRedisEntity.availableIssuePeriodAndTime(requestDate));

            assertNotNull(exception);
            assertEquals(INVALID_EVENT_TIME.formatted(dailyIssueStartTime, dailyIssueEndTime), exception.getMessage());
        }

        @DisplayName("[ERROR] 설정된 이벤트 시간이 null 경우 EventTimeException 발생 ")
        @Test
        void availableIssuePeriodAndTime_with_null() {
            eventRedisEntity = new EventRedisEntity(1L, "category", "description",
                    startDate, endDate, null, null);

            EventTimeException exception = assertThrows(EventTimeException.class, () -> eventRedisEntity.availableIssuePeriodAndTime(validNow));
            assertNotNull(exception);
            assertEquals(EVENT_TIME_IS_NULL.formatted(null, null), exception.getMessage());
        }

        @DisplayName("[SUCCESS] 요청 값이 이벤트 시간 범위인 경우 ")
        @Test
        void availableIssuePeriodAndTime_success() {
            eventRedisEntity = new EventRedisEntity(1L, "category", "description",
                    startDate, endDate, dailyIssueStartTime, dailyIssueEndTime);
            assertDoesNotThrow(() -> eventRedisEntity.availableIssuePeriodAndTime(validNow));
        }
    }
}