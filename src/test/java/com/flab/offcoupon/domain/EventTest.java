package com.flab.offcoupon.domain;

import com.flab.offcoupon.exception.event.EventPeriodException;
import com.flab.offcoupon.exception.event.EventTimeException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.flab.offcoupon.exception.event.ErrorMessage.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class EventTest {

    private Event event;

    @BeforeEach
    void init() {
        event = new Event(1,
                1,
                "바디케어 상품",
                "바디케어 상품 50%할인",
                LocalDate.of(2024,02,01),
                LocalDate.of(2024,02,05),
                "13:00:00",
                "15:00:00",
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    @Test
    @DisplayName("[SUCCESS] 이벤트 발급 기간 검증 성공")
    void event_period_test_success() {
        LocalDateTime testTime = LocalDateTime.of(2024, 02, 01, 13, 0, 0);
        Assertions.assertThat(event.availableIssueDate(testTime)).isTrue();

    }

    @Test
    @DisplayName("[ERROR] 이벤트 발급 기간 검증 실패")
    void event_period_test_fail() {
        LocalDateTime testTime = LocalDateTime.of(2024, 02, 20, 13, 0, 0);
        Assertions.assertThat(event.availableIssueDate(testTime)).isFalse();
    }

    @Test
    @DisplayName("[ERROR] 이벤트 발급 기간이 설정되어 있지 않은 경우")
    void event_period_is_null() {
        event = new Event(1,
                1,
                "바디케어 상품",
                "바디케어 상품 50%할인",
                null,
                null,
                "13:00:00",
                "15:00:00",
                LocalDateTime.now(),
                LocalDateTime.now());
        LocalDateTime testTime = LocalDateTime.of(2024, 02, 20, 13, 0, 0);
        assertThatThrownBy(() -> event.availableIssueDate(testTime))
                .isInstanceOf(EventPeriodException.class)
                .hasMessageContaining(EVENT_PERIOD_IS_NULL.formatted(null, null));
    }

    @Test
    @DisplayName("[SUCCESS] 이벤트 발급 시간 검증 성공")
    void event_time_test_success() {
        LocalDateTime testTime = LocalDateTime.of(2024, 02, 01, 13, 0, 0);
        Assertions.assertThat(event.availableIssueTime(testTime)).isTrue();

    }

    @Test
    @DisplayName("[ERROR] 이벤트 발급 시간 검증 실패")
    void event_time_test_fail() {
        LocalDateTime testTime = LocalDateTime.of(2024, 02, 01, 20, 0, 0);
        Assertions.assertThat(event.availableIssueTime(testTime)).isFalse();
    }

    @Test
    @DisplayName("[ERROR] 이벤트 발급 시간이 설정되어 있지 않은 경우")
    void event_time_is_null() {
        event = new Event(1,
                1,
                "바디케어 상품",
                "바디케어 상품 50%할인",
                LocalDate.of(2024,02,01),
                LocalDate.of(2024,02,05),
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now());
        LocalDateTime testTime = LocalDateTime.of(2024, 02, 20, 13, 0, 0);
        assertThatThrownBy(() -> event.availableIssueTime(testTime))
                .isInstanceOf(EventTimeException.class)
                .hasMessageContaining(EVENT_TIME_IS_NULL.formatted(null, null));
    }

    @Test
    @DisplayName("[ERROR] 이벤트 발급 기간 및 시간이 아닐 경우")
    void event_period_is_not_satisfied() {
        LocalDateTime testTime = LocalDateTime.of(2024, 01, 01, 13, 0, 0);
            assertThatThrownBy(() -> event.availableIssuePeriodAndTime(testTime))
                .isInstanceOf(EventPeriodException.class)
                    .hasMessage(INVALID_EVENT_PERIOD.formatted(
                            LocalDate.of(2024,02,01),LocalDate.of(2024,02,05)));

    }

    @Test
    @DisplayName("[ERROR] 이벤트 발급 시간 및 시간이 아닐 경우")
    void event_time_is_not_satisfied() {
        LocalDateTime testTime = LocalDateTime.of(2024, 02, 01, 20, 0, 0);

        assertThatThrownBy(() -> event.availableIssuePeriodAndTime(testTime))
                .isInstanceOf(EventTimeException.class)
                .hasMessage(INVALID_EVENT_TIME.formatted("13:00:00", "15:00:00"));
    }
}