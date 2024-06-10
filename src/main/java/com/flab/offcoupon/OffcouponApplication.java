package com.flab.offcoupon;

import com.flab.offcoupon.component.scheduler.ScheduleRunner;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@RequiredArgsConstructor
@SpringBootApplication
public class OffcouponApplication {

	private final ScheduleRunner scheduleRunner;
	@PostConstruct
	public void started() {
		// timezone UTC 셋팅
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}
	@PostConstruct
	public void scheduleRun() {
		scheduleRunner.run();
	}
	public static void main(String[] args) {
		SpringApplication.run(OffcouponApplication.class, args);
	}
}
