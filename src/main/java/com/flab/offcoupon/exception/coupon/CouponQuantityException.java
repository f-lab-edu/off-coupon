package com.flab.offcoupon.exception.coupon;

import com.flab.offcoupon.exception.CustomException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponQuantityException extends CustomException {

        public CouponQuantityException(String message) {
            super(message);
        }
}
