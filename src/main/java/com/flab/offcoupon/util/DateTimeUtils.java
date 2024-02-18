package com.flab.offcoupon.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@NoArgsConstructor (access = AccessLevel.PRIVATE)
public class DateTimeUtils {

    public static LocalDateTime nowFromZone() {
        return ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();
    }
}
