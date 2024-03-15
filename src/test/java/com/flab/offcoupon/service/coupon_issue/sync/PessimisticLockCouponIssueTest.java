package com.flab.offcoupon.service.coupon_issue.sync;

import com.flab.offcoupon.domain.entity.Coupon;
import com.flab.offcoupon.domain.entity.CouponType;
import com.flab.offcoupon.domain.entity.DiscountType;
import com.flab.offcoupon.domain.entity.Event;
import com.flab.offcoupon.exception.coupon.CouponNotFoundException;
import com.flab.offcoupon.exception.event.EventNotFoundException;
import com.flab.offcoupon.repository.mysql.CouponRepository;
import com.flab.offcoupon.repository.mysql.EventRepository;
import com.flab.offcoupon.util.ResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.COUPON_NOT_EXIST;
import static com.flab.offcoupon.exception.event.EventErrorMessage.EVENT_NOT_EXIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Transactional
@SpringBootTest
class PessimisticLockCouponIssueTest {
    @Autowired
    private PessimisticLockCouponIssue pessimisticLockCouponIssue;
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CouponRepository couponRepository;

    @BeforeEach
    void setUp(){
        Event event = new Event(
                1L,
                "바디케어",
                "바디케어 전품목 이벤트",
                LocalDate.now(),
                LocalDate.now(),
                "13:00:00",
                "15:00:00",
                LocalDateTime.now(),
                LocalDateTime.now());
        eventRepository.save(event);

        Coupon coupon = new Coupon(
                1L,
                1L,
                DiscountType.PERCENT,
                50L,
                null,
                CouponType.FIRST_COME_FIRST_SERVED,
                500L,
                0L,
                LocalDateTime.now().plusMonths(1L),
                LocalDateTime.now().plusMonths(2L),
                LocalDateTime.now(),
                LocalDateTime.now());
        couponRepository.save(coupon);
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