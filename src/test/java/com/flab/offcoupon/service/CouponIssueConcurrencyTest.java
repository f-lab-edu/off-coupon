package com.flab.offcoupon.service;


import com.flab.offcoupon.domain.entity.Coupon;
import com.flab.offcoupon.repository.CouponIssueRepository;
import com.flab.offcoupon.repository.CouponRepository;
import com.flab.offcoupon.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class CouponIssueConcurrencyTest {
    @Autowired
    private CouponIssueService couponIssueService;

    @Autowired
    private CouponIssueRepository couponIssueRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void 동시에_100개_요청_실패() throws InterruptedException {
        final int threadCount = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int currentThreadCount = i+1;
            executorService.submit(() -> {
                try {
                    LocalDateTime currentDateTime = LocalDateTime.of(2024, 02, 01, 13, 0, 0);
                    couponIssueService.issueCoupon(currentDateTime,1, 1, currentThreadCount);
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