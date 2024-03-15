package com.flab.offcoupon.service.coupon_issue;

import com.flab.offcoupon.domain.entity.Coupon;
import com.flab.offcoupon.domain.entity.CouponType;
import com.flab.offcoupon.domain.entity.DiscountType;
import com.flab.offcoupon.domain.entity.Event;
import com.flab.offcoupon.repository.mysql.CouponRepository;
import com.flab.offcoupon.repository.mysql.EventRepository;
import com.flab.offcoupon.util.ResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class CouponIssueRequestServiceTest {

    @Autowired
    private CouponIssueRequestService couponIssueRequestService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private CouponRepository couponRepository;
    private final static String COUPON_ISSUE_SUCCESS_MESSAGE = "쿠폰이 발급 완료되었습니다. memberId : %s, couponId : %s";
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void clear() {
        Collection<String> redisKeys = redisTemplate.keys("*");
        redisTemplate.delete(redisKeys);
    }
    @BeforeEach
    void setUp() {
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
    @DisplayName("[SUCCESS] 동기식 쿠폰 발급 테스트")
    void SyncIssueCoupon() throws InterruptedException {
        // given
        LocalDateTime currentDateTime = LocalDateTime.now().withHour(13).withMinute(0).withSecond(0);
        long eventId = 1L;
        long couponId = 1L;
        long memberId = 1L;
        // when
        ResponseDTO<String> response = couponIssueRequestService.syncIssueCoupon(currentDateTime, eventId, couponId, memberId);
        // then
        assertEquals(COUPON_ISSUE_SUCCESS_MESSAGE.formatted(memberId, couponId), response.getData());
    }

    @Test
    @DisplayName("[SUCCESS] 비동기식 쿠폰 발급 테스트")
    void AsyncIssueCoupon() {
        // given
        LocalDateTime currentDateTime = LocalDateTime.now().withHour(13).withMinute(0).withSecond(0);
        long eventId = 1L;
        long couponId = 1L;
        long memberId = 1L;
        // when
        ResponseDTO<String> response = couponIssueRequestService.asyncIssueCoupon(currentDateTime, eventId, couponId, memberId);
        // then
        assertEquals(COUPON_ISSUE_SUCCESS_MESSAGE.formatted(memberId, couponId), response.getData());
    }
}