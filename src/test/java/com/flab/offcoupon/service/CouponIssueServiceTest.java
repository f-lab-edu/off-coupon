package com.flab.offcoupon.service;

import com.flab.offcoupon.repository.CouponRepository;
import com.flab.offcoupon.repository.EventRepository;
import com.flab.offcoupon.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CouponIssueServiceTest {

    @InjectMocks
    private CouponIssueService couponIssueService;

    @Mock
    EventRepository eventRepository;
    @Mock
    CouponRepository couponRepository;

    @Test
    @DisplayName("쿠폰 발급 테스트")
    void issueCoupon() {
        // given
        long eventId = 1L;
        long couponId = 1L;
        long memberId = 1L;
        // when
        couponIssueService.issueCoupon(eventId, couponId, memberId);
        // then
    }

}