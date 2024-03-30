package com.flab.offcoupon.service.coupon_issue.async.consumer;

import com.flab.offcoupon.component.lock.DistributeLockExecutorWithRedisson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.flab.offcoupon.util.LockMagicNumberConstants.LOCK_LEASE_MILLI_SECOND;
import static com.flab.offcoupon.util.LockMagicNumberConstants.LOCK_WAIT_MILLI_SECOND;

@RequiredArgsConstructor
@Service
public class RedissonLockHandler {

    private final DistributeLockExecutorWithRedisson distributeLockExecutorWithRedisson;
    private final CouponIssueMessageHandler couponIssueMessageHandler;

    public void asyncIssueCoupon() {
        /**
         * 비동기식 쿠폰 발급을 위한 레디스 락을 획득합니다.
         *  락을 사용한 이유는 Schedule 시 작업 처리 시간이 명확하지 않기 때문에 경합이 발생할 것이라고 예상했기 때문입니다.
         */
        distributeLockExecutorWithRedisson.execute(
                "redisson_lock" + "async", LOCK_WAIT_MILLI_SECOND, LOCK_LEASE_MILLI_SECOND, couponIssueMessageHandler
                        ::updateTotalIssuedCouponAndCheckFlagForCompletedIssue);
    }
}
