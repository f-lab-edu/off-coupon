package com.flab.offcoupon.domain.redis;


import com.flab.offcoupon.model.Positive;
import lombok.Getter;

@Getter
public final class IssueRequestKey {
    private final long couponId;
    private final long memberId;

    public IssueRequestKey(Positive couponId, Positive memberId) {
        this.couponId = couponId.getValue();
        this.memberId = memberId.getValue();
    }
}
