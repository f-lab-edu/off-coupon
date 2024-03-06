package com.flab.offcoupon.service;

import com.flab.offcoupon.domain.entity.Coupon;
import com.flab.offcoupon.domain.entity.CouponType;
import com.flab.offcoupon.domain.entity.DiscountType;
import com.flab.offcoupon.exception.coupon.CouponNotFoundException;
import com.flab.offcoupon.exception.coupon.CouponQuantityException;
import com.flab.offcoupon.exception.event.EventNotFoundException;
import com.flab.offcoupon.repository.CouponIssueRepository;
import com.flab.offcoupon.repository.CouponRepository;
import com.flab.offcoupon.repository.EventRepository;
import com.flab.offcoupon.service.couponIssue.CouponIssueService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.COUPON_NOT_EXIST;
import static com.flab.offcoupon.exception.event.EventErrorMessage.EVENT_NOT_EXIST;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@ExtendWith(MockitoExtension.class)
class CouponIssueServiceTest {

    @InjectMocks
    private CouponIssueService couponIssueService;
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
        assertThatThrownBy(() -> couponIssueService.findEvent(eventId))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessage(EVENT_NOT_EXIST.formatted(eventId));
    }
    @Test
    @DisplayName("[ERROR] 쿠폰 식별자가 존재하지 않으면 Exception 발생")
    void issueCoupon_fail_invalid_couponId() {
        // given
        long couponId = 1L;
        // when
        assertThatThrownBy(() -> couponIssueService.findCoupon(couponId))
                .isInstanceOf(CouponNotFoundException.class)
                .hasMessage(COUPON_NOT_EXIST.formatted(couponId));
    }
}