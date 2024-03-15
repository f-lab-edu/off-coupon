package com.flab.offcoupon.service.coupon_issue.concurrency;

import com.flab.offcoupon.domain.entity.Coupon;
import com.flab.offcoupon.repository.mysql.CouponRepository;
import com.flab.offcoupon.service.coupon_issue.sync.LettuceLockCouponIssue;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled("using only for concurrent testing")
@SpringBootTest
class LettuceLockCouponIssueConcurrencyTest {
    @Autowired
    private LettuceLockCouponIssue lettuceLockCouponIssue;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private TransactionTemplate transactionTemplate; // TransactionTemplate 추가

    @Transactional
    @Test
    void 동시에_100개_요청() throws Exception {
        final int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int currentMemberId = i + 1;
            executorService.submit(() -> {
                try {
                        LocalDateTime currentDateTime = LocalDateTime.of(2024, 02, 27, 13, 0, 0);
                        lettuceLockCouponIssue.issueCoupon(currentDateTime, 1, 1, currentMemberId);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        latch.countDown();
                    }
            });
        }
        latch.await();

        Coupon coupon = couponRepository.findCouponById(1).orElseThrow();
        boolean actualTransactionActive3 = TransactionSynchronizationManager.isActualTransactionActive();
        System.out.println("끝 actualTransactionActive = " + actualTransactionActive3);

        // 500 - 100 == 400
        assertEquals(400, coupon.remainedCoupon());
    }
}