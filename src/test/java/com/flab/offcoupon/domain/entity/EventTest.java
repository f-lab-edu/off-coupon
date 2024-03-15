package com.flab.offcoupon.domain.entity;

import com.flab.offcoupon.domain.entity.params.EventPeriodAndTimeParams;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    @Test
    @DisplayName("[SUCCESS] 이벤트 객체 생성")
    void create_event() {
        EventPeriodAndTimeParams eventPeriodAndTimeParams = new EventPeriodAndTimeParams(
                LocalDate.now(),
                LocalDate.now(),
                "13:00:00",
                "15:00:00");
        Event event = Event.create(
                "바디케어",
                "바디케어 전품목 이벤트",
                eventPeriodAndTimeParams);
        assertThat(event).isNotNull();
    }
}