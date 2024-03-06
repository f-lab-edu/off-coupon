package com.flab.offcoupon.service.couponIssue;

import com.flab.offcoupon.util.ResponseDTO;

import java.time.LocalDateTime;

public interface CouponIssueFacade {
    ResponseDTO issueCoupon(LocalDateTime currentDateTime, long eventId, long couponId, long memberId) throws InterruptedException;
}
