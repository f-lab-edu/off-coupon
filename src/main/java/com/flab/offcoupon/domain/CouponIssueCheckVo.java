package com.flab.offcoupon.domain;


import java.time.LocalDate;

public record CouponIssueCheckVo(long memberId, long couponId, LocalDate currentDateTime) {
}
