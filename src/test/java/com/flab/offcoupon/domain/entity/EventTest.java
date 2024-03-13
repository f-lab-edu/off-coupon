package com.flab.offcoupon.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Test
    @DisplayName("[SUCCESS] 이벤트 객체 생성")
    void create_event() {
        Event event = Event.create(
                "바디케어",
                "바디케어 전품목 이벤트",
                LocalDate.now(),
                LocalDate.now(),
                "13:00:00",
                "15:00:00");
        assertThat(event).isNotNull();
    }
}