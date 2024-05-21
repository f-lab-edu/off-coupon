package com.flab.offcoupon.domain.redis;


import com.flab.offcoupon.model.PositiveLong;
import lombok.Getter;

@Getter
public final class IssueRequestKey {
    private final long couponId;
    private final long memberId;

    public IssueRequestKey(PositiveLong couponId, PositiveLong memberId) {
        this.couponId = couponId.getValue();
        this.memberId = memberId.getValue();
    }
}
