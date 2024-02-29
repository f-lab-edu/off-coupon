package com.flab.offcoupon.exception.coupon;

import com.flab.offcoupon.exception.CustomException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponQuantityException extends CustomException {
    public CouponQuantityException(String message) {
            super(message);
        }
}
