package com.flab.offcoupon.service.coupon_issue.async.consumer;

import com.flab.offcoupon.component.lock.DistributeLockExecutorWithRedisson;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

import static com.flab.offcoupon.util.LockMagicNumberConstants.LOCK_LEASE_MILLI_SECOND;
import static com.flab.offcoupon.util.LockMagicNumberConstants.LOCK_WAIT_MILLI_SECOND;

@RequiredArgsConstructor
@Service
public class RedissonLockHandler {

    private final DistributeLockExecutorWithRedisson distributeLockExecutorWithRedisson;
    private final CouponIssueMessageHandler couponIssueMessageHandler;

    public void asyncIssueCoupon() {
        AtomicReference<ResponseDTO<String>> responseDTO = new AtomicReference<>();
        distributeLockExecutorWithRedisson.execute("redisson_lock" + "async", LOCK_WAIT_MILLI_SECOND, LOCK_LEASE_MILLI_SECOND, () ->
                couponIssueMessageHandler.ddd());
    }
}
