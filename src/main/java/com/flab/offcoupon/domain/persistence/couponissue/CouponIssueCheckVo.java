package com.flab.offcoupon.domain.persistence.couponissue;


import java.time.LocalDate;

public record CouponIssueCheckVo(long memberId, long couponId, LocalDate currentDateTime) {
}
