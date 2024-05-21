package com.flab.offcoupon.exception.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)

public class InvalidProductPriceException extends RuntimeException {
    public static final String INVALID_PRODUCT_PRICE = "상품 가격은 0보다 작을 수 없습니다.";
    public InvalidProductPriceException() {
        super(INVALID_PRODUCT_PRICE);
    }
}
