package com.flab.offcoupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OptimisticLockFacade {

    private final CouponIssueService couponIssueService;

    public void issueCoupon(LocalDateTime currentDateTime, long eventId, long couponId, long memberId) throws InterruptedException {
        int retryCount = 0;
        while (true) {
            try {
                couponIssueService.issueCoupon(currentDateTime, eventId, couponId, memberId);
                break;
            } catch (Exception e) {
                // retry
                Thread.sleep(50);
            } finally {
                retryCount++;
                if (retryCount > 10) {
                    throw new RuntimeException("쿠폰발급 실패");

                }
            }
        }
    }
}
