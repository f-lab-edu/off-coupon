package com.flab.offcoupon.service.couponIssue;

import com.flab.offcoupon.repository.RedisLockRepository;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class LettuceLockCouponIssue implements CouponIssueFacade {

    private final RedisLockRepository redisLockRepository;
    private final CouponIssueService couponIssueService;

    @Override
    public ResponseDTO issueCoupon(LocalDateTime currentDateTime, long eventId, long couponId, long memberId) throws InterruptedException {
        ResponseDTO responseDTO = null;
        while (!redisLockRepository.lock(couponId)) {
            Thread.sleep(100);
        }
        try {
            // 쿠폰발급 비즈니스 로직
            responseDTO = couponIssueService.issueCoupon(currentDateTime, eventId, couponId, memberId);
        } finally {
            // 락 해제
            redisLockRepository.unlock(couponId);
        }
        return responseDTO;
    }
}
