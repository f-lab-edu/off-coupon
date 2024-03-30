package com.flab.offcoupon;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class OffcouponApplication {
	@PostConstruct
	public void started() {
		// timezone UTC 셋팅
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}
	public static void main(String[] args) {
		SpringApplication.run(OffcouponApplication.class, args);
	}
}
