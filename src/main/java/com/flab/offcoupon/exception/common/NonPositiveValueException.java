package com.flab.offcoupon.exception.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NonPositiveValueException extends RuntimeException {
    public static final String MUST_BE_POSITIVE = "0보다 무조건 커야 합니다."; // 양수만 허용
    public static final String MUST_NOT_BE_NEGATIVE = "0보다 작을 수 없습니다."; // 0 + 양수 허용
    public NonPositiveValueException(String message) {
        super(message);
    }
}
