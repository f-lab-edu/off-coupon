package com.flab.offcoupon.domain;

import java.time.LocalDateTime;

public class CouponIssue {
    private long couponIssueId;
    private long memberId;
    private long couponId;
    private CouponStatus couponStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
