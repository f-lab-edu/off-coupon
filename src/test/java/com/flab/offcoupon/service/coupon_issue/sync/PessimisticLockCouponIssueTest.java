package com.flab.offcoupon.service.coupon_issue.sync;

import com.flab.offcoupon.exception.coupon.CouponNotFoundException;
import com.flab.offcoupon.exception.event.EventNotFoundException;
import com.flab.offcoupon.repository.mysql.CouponRepository;
import com.flab.offcoupon.repository.mysql.EventRepository;
import com.flab.offcoupon.setup.SetupUtils;
import com.flab.offcoupon.util.ResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.COUPON_NOT_EXIST;
import static com.flab.offcoupon.exception.event.EventErrorMessage.EVENT_NOT_EXIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
class PessimisticLockCouponIssueTest {
    @Autowired
    private PessimisticLockCouponIssue pessimisticLockCouponIssue;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CouponRepository couponRepository;
    private SetupUtils setupUtils = new SetupUtils();

    @BeforeEach
    void setUp() {
        setupUtils.setUpEventAndCoupon(eventRepository, couponRepository);
    }

    @Test
    @DisplayName("[ERROR] 쿠폰 발급 - 이벤트 식별자가 존재하지 않으면 Exception 발생")
    void issueCoupon_fail_with_invalid_eventId() {
        // given
        LocalDateTime currentDateTime = LocalDateTime.now().withHour(13).withMinute(0).withSecond(0);
        long invalidEventId = 2L;
        long couponId = 1L;
        long memberId = 1L;
        // when
        assertThatThrownBy(() -> pessimisticLockCouponIssue.issueCoupon(currentDateTime, invalidEventId, couponId, memberId))
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
        // when
        assertThatThrownBy(() -> pessimisticLockCouponIssue.issueCoupon(currentDateTime, eventId, invalidCouponId, memberId))
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
        // when
        ResponseDTO responseDTO = pessimisticLockCouponIssue.issueCoupon(currentDateTime, eventId, couponId, memberId);
        assertThat(responseDTO.getData()).isEqualTo("쿠폰이 발급 완료되었습니다. memberId : %s, couponId : %s".formatted(memberId, couponId));
    }
}