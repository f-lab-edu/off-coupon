package com.flab.offcoupon.component.scheduler;

import org.springframework.stereotype.Component;

import com.flab.offcoupon.component.scheduler.coupon_issue.ConsumeMqScheduler;
import com.flab.offcoupon.component.scheduler.coupon_issue.UpdateTotalCouponIssueCntScheduler;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ScheduleRunner {

	private final ConsumeMqScheduler consumeMqScheduler;
	private final UpdateTotalCouponIssueCntScheduler updateTotalCouponIssueCntScheduler;

	public void run() {
		consumeMqScheduler.startScheduler();
		updateTotalCouponIssueCntScheduler.startScheduler();
	}
}
