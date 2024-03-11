package com.flab.offcoupon.service;

import com.flab.offcoupon.exception.coupon.CouponNotFoundException;
import com.flab.offcoupon.exception.event.EventNotFoundException;
import com.flab.offcoupon.repository.mysql.CouponIssueRepository;
import com.flab.offcoupon.repository.mysql.CouponRepository;
import com.flab.offcoupon.repository.mysql.EventRepository;
import com.flab.offcoupon.service.couponIssue.sync.DefaultCouponIssueService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.COUPON_NOT_EXIST;
import static com.flab.offcoupon.exception.event.EventErrorMessage.EVENT_NOT_EXIST;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@ExtendWith(MockitoExtension.class)
class DefaultCouponIssueServiceTest {

    @InjectMocks
    private DefaultCouponIssueService defaultCouponIssueService;
    @Mock
    EventRepository eventRepository;
    @Mock
    CouponRepository couponRepository;

    @Mock
    CouponIssueRepository couponIssueRepository;

    @Test
    @DisplayName("[ERROR] 이벤트 식별자가 존재하지 않으면 Exception 발생")
    void issueCoupon_fail_invalid_eventId() {
        // given
        long eventId = 1L;
        // when
        assertThatThrownBy(() -> defaultCouponIssueService.findEvent(eventId))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessage(EVENT_NOT_EXIST.formatted(eventId));
    }
    @Test
    @DisplayName("[ERROR] 쿠폰 식별자가 존재하지 않으면 Exception 발생")
    void issueCoupon_fail_invalid_couponId() {
        // given
        long couponId = 1L;
        // when
        assertThatThrownBy(() -> defaultCouponIssueService.findCoupon(couponId))
                .isInstanceOf(CouponNotFoundException.class)
                .hasMessage(COUPON_NOT_EXIST.formatted(couponId));
    }
}