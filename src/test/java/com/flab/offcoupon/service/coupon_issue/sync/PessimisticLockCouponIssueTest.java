package com.flab.offcoupon.service.coupon_issue.sync;

import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.*;
import static com.flab.offcoupon.exception.event.EventErrorMessage.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.flab.offcoupon.dto.request.IssueRequestParameter;
import com.flab.offcoupon.exception.coupon.CouponNotFoundException;
import com.flab.offcoupon.exception.event.EventNotFoundException;
import com.flab.offcoupon.model.PositiveLong;
import com.flab.offcoupon.repository.mysql.CouponIssueRepository;
import com.flab.offcoupon.repository.mysql.CouponRepository;
import com.flab.offcoupon.repository.mysql.EventRepository;
import com.flab.offcoupon.setup.SetupInitializer;
import com.flab.offcoupon.util.ResponseDTO;

@SpringBootTest
@Transactional
class PessimisticLockCouponIssueTest {
	@Autowired
	private PessimisticLockCouponIssue pessimisticLockCouponIssue;

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private CouponRepository couponRepository;

	@Autowired
	private CouponIssueRepository couponIssueRepository;
	private SetupInitializer setupInitializer;

	@BeforeEach
	void setUp() {
		setupInitializer = new SetupInitializer(eventRepository, couponRepository);
		setupInitializer.setUpEventAndCoupon();
	}

	@AfterEach
	void clear() {
		couponIssueRepository.deleteCouponIssueByMemberIdAndCouponId(1L, 1L);
	}

	@Test
	@DisplayName("[ERROR] 쿠폰 발급 - 이벤트 식별자가 존재하지 않으면 Exception 발생")
	void issueCoupon_fail_with_invalid_eventId() {
		// given
		LocalDateTime currentDateTime = LocalDateTime.now().withHour(13).withMinute(0).withSecond(0);
		long invalidEventId = 1000L;
		long couponId = 1L;
		long memberId = 1L;
		IssueRequestParameter parameter = new IssueRequestParameter(new PositiveLong(invalidEventId),
			new PositiveLong(couponId), new PositiveLong(memberId));

		// when
		assertThatThrownBy(() -> pessimisticLockCouponIssue.issueCoupon(currentDateTime, parameter))
			.isInstanceOf(EventNotFoundException.class)
			.hasMessage(EVENT_NOT_EXIST.formatted(invalidEventId));
	}

	@Test
	@DisplayName("[ERROR] 쿠폰 발급 - 쿠폰 식별자가 존재하지 않으면 Exception 발생")
	void issueCoupon_fail_with_invalid_couponId() {
		// given
		LocalDateTime currentDateTime = LocalDateTime.now().withHour(13).withMinute(0).withSecond(0);
		long eventId = 1L;
		long invalidCouponId = 2L;
		long memberId = 1L;
		IssueRequestParameter parameter = new IssueRequestParameter(new PositiveLong(eventId),
			new PositiveLong(invalidCouponId), new PositiveLong(memberId));

		// when
		assertThatThrownBy(() -> pessimisticLockCouponIssue.issueCoupon(currentDateTime, parameter))
			.isInstanceOf(CouponNotFoundException.class)
			.hasMessage(COUPON_NOT_EXIST.formatted(invalidCouponId));
	}

	@Test
	@DisplayName("[SUCCESS] 쿠폰 발급 성공")
	void issueCoupon_success() {
		// given
		LocalDateTime currentDateTime = LocalDateTime.now().withHour(13).withMinute(0).withSecond(0);
		long eventId = 1L;
		long couponId = 1L;
		long memberId = 1L;
		IssueRequestParameter parameter = new IssueRequestParameter(new PositiveLong(eventId),
			new PositiveLong(couponId), new PositiveLong(memberId));

		// when
		ResponseDTO responseDTO = pessimisticLockCouponIssue.issueCoupon(currentDateTime, parameter);
		assertThat(responseDTO.getData()).isEqualTo(
			"쿠폰이 발급 완료되었습니다. memberId : %s, couponId : %s".formatted(memberId, couponId));
	}
}