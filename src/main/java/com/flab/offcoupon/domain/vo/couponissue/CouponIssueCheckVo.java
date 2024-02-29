package com.flab.offcoupon.domain.vo.couponissue;


import java.time.LocalDate;

public record CouponIssueCheckVo(long memberId, long couponId, LocalDate currentDateTime) {
}
