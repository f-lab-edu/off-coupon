package com.flab.offcoupon.service.couponIssue;

import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class RedissonLockCouponIssue implements CouponIssueFacade {

    private final RedissonClient redissonClient;
    private final CouponIssueService couponIssueService;
    @Override
    public ResponseDTO issueCoupon(LocalDateTime currentDateTime, long eventId, long couponId, long memberId) throws InterruptedException {
        ResponseDTO responseDTO = null;
        RLock lock = redissonClient.getLock(String.valueOf(couponId));
        try {
            // waitTime(락 획득 시도 시간) : 15초, leaseTime(락 점유 시간) : 1초
            if (!lock.tryLock(15, 1, TimeUnit.SECONDS)) {
                throw new InterruptedException("redisson 락 획득 실패");
            }
            responseDTO = couponIssueService.issueCoupon(currentDateTime, eventId, couponId, memberId);
        } catch ( InterruptedException e) {
            throw  new RuntimeException(e);
        } finally {
            lock.unlock();
        }
        return responseDTO;
    }
}
