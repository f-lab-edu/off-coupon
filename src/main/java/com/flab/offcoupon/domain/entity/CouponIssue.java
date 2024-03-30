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
    private final boolean checkRelatedIssuedQuantity;

    private CouponIssue(long memberId, long couponId, CouponStatus couponStatus, LocalDateTime createdAt, LocalDateTime updatedAt, boolean checkRelatedIssuedQuantity) {
        this.memberId = memberId;
        this.couponId = couponId;
        this.couponStatus = couponStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.checkRelatedIssuedQuantity = checkRelatedIssuedQuantity;
    }
    public static CouponIssue create(long memberId, long couponId) {
        LocalDateTime now = DateTimeUtils.nowFromZone();
        return new CouponIssue( memberId, couponId, CouponStatus.NOT_ACTIVE, now, now, false);
    }

    /**
     * 쿠폰 발급 완료 후 반정규화 칼럼(Coupon테이블의 issued_quantity)이 업데이트 되었을 경우
     * checkRelatedIssuedQuantity를 true로 변경합니다.
     * @param couponIssue
     * @return CouponIssue
     */
    public CouponIssue updateCheckFlag(CouponIssue couponIssue) {
        return new CouponIssue(
                couponIssue.id,
                couponIssue.memberId,
                couponIssue.couponId,
                couponIssue.couponStatus,
                couponIssue.createdAt,
                couponIssue.updatedAt,
                true);
    }
}
