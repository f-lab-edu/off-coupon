package com.flab.offcoupon.exception.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NonPositiveValueException extends RuntimeException {
    private static final String MUST_BE_POSITIVE = "0보다 큰 값을 입력해야 합니다.";
    public NonPositiveValueException() {
        super(MUST_BE_POSITIVE);
    }
}
