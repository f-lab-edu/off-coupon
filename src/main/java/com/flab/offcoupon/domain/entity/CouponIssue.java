package com.flab.offcoupon.domain.entity;

import com.flab.offcoupon.util.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@AllArgsConstructor
public final class CouponIssue {
    private long id;
    private final long memberId;
    private final long couponId;
    private final CouponStatus couponStatus;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private CouponIssue(long memberId, long couponId, CouponStatus couponStatus, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.memberId = memberId;
        this.couponId = couponId;
        this.couponStatus = couponStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public static CouponIssue create (long memberId, long couponId) {
        LocalDateTime now = DateTimeUtils.nowFromZone();
        return new CouponIssue( memberId, couponId, CouponStatus.NOT_ACTIVE, now, now);
    }
}
