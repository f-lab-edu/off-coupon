package com.flab.offcoupon.component.scheduler.coupon_issue;

import com.flab.offcoupon.component.scheduler.DynamicScheduler;
import com.flab.offcoupon.service.coupon_issue.async.consumer.CouponIssueConsumer;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * RabbitMQ를 이용한 비동기 쿠폰 발행을 처리하는 컨슈머 클래스와 스케줄러를 연결하는 클래스입니다.
 */
@Component
public class ConsumeMqScheduler {
    private CouponIssueConsumer couponIssueConsumer;
    public ConsumeMqScheduler(CouponIssueConsumer couponIssueConsumer) {
        this.couponIssueConsumer = couponIssueConsumer;
    }
    // 실행 로직
    private final Runnable runnable = () -> {
        // 스케줄러가 실행할 작업
        couponIssueConsumer.consumeCouponIssueMessage();
    };

    // 실행 주기
    private final Trigger trigger = new PeriodicTrigger(3, TimeUnit.SECONDS);

    public void startScheduler() {
        DynamicScheduler scheduler = new DynamicScheduler(runnable, trigger);
        scheduler.startScheduler();
    }
}
