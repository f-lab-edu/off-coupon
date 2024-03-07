package com.flab.offcoupon.service.couponIssue.concurrency;

import com.flab.offcoupon.domain.entity.Coupon;
import com.flab.offcoupon.repository.mysql.CouponRepository;
import com.flab.offcoupon.service.couponIssue.sync.RedissonLockCouponIssue;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("using only for concurrent testing")
@SpringBootTest
class RedissonLockCouponIssueTest {

    @Autowired
    private RedissonLockCouponIssue redissonLockCouponIssue;
    @Autowired
    private CouponRepository couponRepository;
    @Test
    public void 동시에_100개_요청() throws Exception {
        final int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int currentMemberId = i+1;
            executorService.submit(() -> {
                try {
                    LocalDateTime currentDateTime = LocalDateTime.of(2024, 02, 27, 13, 0, 0);
                    redissonLockCouponIssue.issueCoupon(currentDateTime,1, 1,currentMemberId);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Coupon coupon = couponRepository.findCouponById(1).orElseThrow();
        // 500 - 100 == 400
        assertEquals(400,coupon.remainedCoupon());
    }
}