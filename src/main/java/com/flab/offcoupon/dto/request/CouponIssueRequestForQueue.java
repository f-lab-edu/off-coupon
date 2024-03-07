package com.flab.offcoupon.dto.request;

public record CouponIssueRequestForQueue (
        long couponId,
        long userId
){
}
