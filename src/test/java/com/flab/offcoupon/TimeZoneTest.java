package com.flab.offcoupon;

import com.flab.offcoupon.util.DateTimeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class TimeZoneTest{

    @Test
    void springBootTimezoneTest() {
        /* 국제 표준 시간 */
        LocalDateTime globalTime = LocalDateTime.now();
        /* 한국 시간 */
        LocalDateTime koreaTime = DateTimeUtils.nowFromZone();

        assertThat(globalTime).isNotNull();
        assertThat(koreaTime).isNotNull();
        assertThat(globalTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .isNotEqualTo(koreaTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
