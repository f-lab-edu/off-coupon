package com.flab.offcoupon.exception.coupon;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponErrorMessage {
    public static final String COUPON_NOT_EXIST = "존재하지 않는 쿠폰입니다. couponId : %s";
    public static final String INVALID_COUPON_QUANTITY = "발급 가능한 쿠폰이 없습니다. tatal : %s, issued : %s";
    public static final String ASYNC_INVALID_COUPON_QUANTITY = "발급 가능한 수량을 초과합니다. couponId : %s";
    public static final String COUPON_QUANTITY_IS_NULL =  "쿠폰의 발급 가능 수량이 설정 되어있지 않습니다. tatal : %s, issued : %s";
    public static final String DUPLICATED_COUPON = "이미 발급된 쿠폰입니다. memberId : %s, couponId : %s";
    public static final String ASYNC_DUPLICATED_COUPON = "이미 발급 요청이 처리됐습니다. memberId : %s, couponId : %s";
    public static final String FAIL_COUPON_ISSUE_REQUEST = "쿠폰 발급에 실패했습니다. input: %s";

}
