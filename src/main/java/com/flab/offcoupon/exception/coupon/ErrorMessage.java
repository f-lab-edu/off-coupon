package com.flab.offcoupon.exception.coupon;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessage {

    public static final String INVALID_COUPON_QUANTITY = "발급 가능한 쿠폰이 없습니다. tatal : %s, issued : %s";
    public static final String COUPON_QUANTITY_IS_NULL =  "쿠폰의 발급 가능 수량이 설정 되어있지 않습니다. tatal : %s, issued : %s";
}
