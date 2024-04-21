package com.flab.offcoupon.component.scheduler.coupon_issue;

import com.flab.offcoupon.component.scheduler.DynamicScheduler;
import com.flab.offcoupon.service.coupon_issue.async.consumer.CouponIssueConsumer;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 비동기 쿠폰 발행  쿠폰 발행 총 갯수를 업데이트하는 스케줄러를 연결하는 클래스입니다.
 */
@Component
public class UpdateTotalCouponIssueCntScheduler {
    private CouponIssueConsumer couponIssueConsumer;

    public UpdateTotalCouponIssueCntScheduler(CouponIssueConsumer couponIssueConsumer) {
        this.couponIssueConsumer = couponIssueConsumer;
    }

    private final Runnable runnable = () -> {
        // 스케줄러가 실행할 작업
        couponIssueConsumer.updateTotalCouponIssueCount();
    };

    private final Trigger trigger = new PeriodicTrigger(10, TimeUnit.SECONDS);

    public void startScheduler() {
        DynamicScheduler scheduler = new DynamicScheduler(runnable, trigger);
        scheduler.startScheduler();
    }
}
