package com.flab.offcoupon.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;


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
}