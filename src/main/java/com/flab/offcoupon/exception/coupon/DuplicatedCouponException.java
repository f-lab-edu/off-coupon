package com.flab.offcoupon.exception.coupon;

import com.flab.offcoupon.exception.CustomException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DuplicatedCouponException extends CustomException {
    public DuplicatedCouponException(String message) {
        super(message);
    }
}
