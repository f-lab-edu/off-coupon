package com.flab.offcoupon.service.couponIssue.sync;

import com.flab.offcoupon.service.couponIssue.async.AsyncCouponIssueServiceV1;
import com.flab.offcoupon.util.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CouponIssueRequestService {

    private final CouponIssueFacade couponIssueFacade;
    private final AsyncCouponIssueServiceV1 asyncCouponIssueServiceV1;
    // pessimisticLockCouponIssue, namedLockCouponIssue, lettuceLockCouponIssue, redissonLockCouponIssue
    @Autowired
    public CouponIssueRequestService(@Qualifier("redissonLockCouponIssue") CouponIssueFacade couponIssueFacade,
                                     AsyncCouponIssueServiceV1 asyncCouponIssueServiceV1 ) {
        this.couponIssueFacade = couponIssueFacade;
        this.asyncCouponIssueServiceV1 = asyncCouponIssueServiceV1;
    }

    public ResponseDTO syncIssueCoupon(LocalDateTime currentDateTime, long eventId, long couponId, long memberId) throws InterruptedException {
        return couponIssueFacade.issueCoupon(currentDateTime, eventId, couponId, memberId);
    }

    public ResponseDTO asyncIssueCoupon(LocalDateTime currentDateTime, long eventId, long couponId, long memberId) throws InterruptedException {
        return asyncCouponIssueServiceV1.issueCoupon(currentDateTime, eventId, couponId, memberId);
    }
}
