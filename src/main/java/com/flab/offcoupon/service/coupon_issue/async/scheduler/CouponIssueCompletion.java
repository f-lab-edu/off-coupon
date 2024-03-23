package com.flab.offcoupon.service.coupon_issue.async.scheduler;

import com.flab.offcoupon.repository.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@EnableScheduling
@Component
public class CouponIssueCompletion {

    private final RedisRepository redisRepository;
    @Scheduled(fixedDelay = 1000) // scheduled annotation을 사용하여 1초마다 실행되도록 설정
    public void completeCouponIssue() {
        // 쿠폰 발급 완료 처리
    }
}
