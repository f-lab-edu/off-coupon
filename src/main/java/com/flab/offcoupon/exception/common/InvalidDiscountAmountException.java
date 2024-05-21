package com.flab.offcoupon.exception.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidDiscountAmountException extends RuntimeException {
    public static final String INVALID_DISCOUNT_AMOUNT = "할인 쿠폰이 적용됐는데 할인 금액은 0보다 작거나 같을 수 없습니다.";
    public InvalidDiscountAmountException() {
        super(INVALID_DISCOUNT_AMOUNT);
    }
}
