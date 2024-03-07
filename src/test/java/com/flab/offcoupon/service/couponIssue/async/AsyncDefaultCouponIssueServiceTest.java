package com.flab.offcoupon.service.couponIssue.async;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.offcoupon.domain.entity.*;
import com.flab.offcoupon.dto.request.CouponIssueRequestForQueue;
import com.flab.offcoupon.exception.coupon.CouponNotFoundException;
import com.flab.offcoupon.exception.coupon.CouponQuantityException;
import com.flab.offcoupon.exception.coupon.DuplicatedCouponException;
import com.flab.offcoupon.exception.event.EventPeriodException;
import com.flab.offcoupon.exception.event.EventTimeException;
import com.flab.offcoupon.service.couponIssue.sync.DefaultCouponIssueService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.LongStream;

import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.*;
import static com.flab.offcoupon.exception.event.EventErrorMessage.INVALID_EVENT_PERIOD;
import static com.flab.offcoupon.exception.event.EventErrorMessage.INVALID_EVENT_TIME;
import static com.flab.offcoupon.util.CouponRedisUtils.getIssueRequestKey;
import static com.flab.offcoupon.util.CouponRedisUtils.getIssueRequestQueueKey;

@SpringBootTest
class AsyncDefaultCouponIssueServiceTest {

    @Autowired
    AsyncCouponIssueService asyncCouponIssueService;

    @Autowired
    DefaultCouponIssueService defaultCouponIssueService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void clear() {
        Collection<String> redisKeys = redisTemplate.keys("*");
        redisTemplate.delete(redisKeys);
    }

    @Test
    @DisplayName("쿠폰 발급 - 쿠폰이 존재하지 않는다면 예외를 반환한다")
    void test() {
        // given
        LocalDateTime currentDateTime = LocalDateTime.of(2024, 02, 27, 13, 0, 0);
        long eventId = 1L;
        long couponId = 2L;
        long memberId = 1L;
        // when & then
        CouponNotFoundException exception = Assertions.assertThrows(CouponNotFoundException.class, () -> {
            asyncCouponIssueService.issueCoupon(currentDateTime, eventId, couponId, memberId);
        });
        Assertions.assertEquals(exception.getMessage(), COUPON_NOT_EXIST.formatted(couponId));
    }

    @Test
    @DisplayName("쿠폰 발급 - 쿠폰 발급 수량이 존재하지 않는다면 예외를 반환한다")
    void test_2() {
        // given
        LocalDateTime currentDateTime = LocalDateTime.of(2024, 02, 27, 13, 0, 0);
        long memberId = 1000L;
        Coupon coupon = defaultCouponIssueService.findCoupon(1L);

        LongStream.range(0L, coupon.getMaxQuantity()).forEach(idx -> {
            redisTemplate.opsForSet().add(getIssueRequestKey(coupon.getId()), String.valueOf(idx));
        });
        // when & then
        CouponQuantityException exception = Assertions.assertThrows(CouponQuantityException.class, () -> {
            asyncCouponIssueService.issueCoupon(currentDateTime, 1L, coupon.getId(), memberId);
        });
        Assertions.assertEquals(exception.getMessage(), ASYNC_INVALID_COUPON_QUANTITY.formatted(coupon.getId()));
    }

    @Test
    @DisplayName("쿠폰 발급 - 이미 발급된 유저라면 예외를 반환한다")
    void issue_3() {
        // given
        LocalDateTime currentDateTime = LocalDateTime.of(2024, 02, 27, 13, 0, 0);
        long memberId = 1L;
        Coupon coupon = defaultCouponIssueService.findCoupon(1L);
        redisTemplate.opsForSet().add(getIssueRequestKey(coupon.getId()), String.valueOf(memberId));
        // when & then
        DuplicatedCouponException exception = Assertions.assertThrows(DuplicatedCouponException.class, () -> {
            asyncCouponIssueService.issueCoupon(currentDateTime, 1L, coupon.getId(), memberId);
        });
        Assertions.assertEquals(exception.getMessage(),ASYNC_DUPLICATED_COUPON.formatted(memberId, coupon.getId()));
    }

    @Test
    @DisplayName("쿠폰 발급 - 발급 기간이 일치하지 않는다면 예외를 반환한다")
    void issue_4() {
        // given
        LocalDateTime currentDateTime = LocalDateTime.of(2024, 02, 27, 13, 0, 0).plusDays(2);
        long memberId = 1L;
        long eventId = 1L;
        long couponId = 1L;
        Event event = defaultCouponIssueService.findEvent(eventId);
        // when & then
        EventPeriodException exception = Assertions.assertThrows(EventPeriodException.class, () -> {
            asyncCouponIssueService.issueCoupon(currentDateTime, eventId, couponId, memberId);
        });
        Assertions.assertEquals(exception.getMessage(),INVALID_EVENT_PERIOD.formatted(event.getStartDate(), event.getEndDate()));
    }

    @Test
    @DisplayName("쿠폰 발급 - 발급 시간이 일치하지 않는다면 예외를 반환한다")
    void issue_5() {
        // given
        LocalDateTime currentDateTime = LocalDateTime.of(2024, 02, 27, 13, 0, 0).plusHours(4);
        long memberId = 1L;
        long eventId = 1L;
        long couponId = 1L;
        Event event = defaultCouponIssueService.findEvent(eventId);
        // when & then
        EventTimeException exception = Assertions.assertThrows(EventTimeException.class, () -> {
            asyncCouponIssueService.issueCoupon(currentDateTime, eventId, couponId, memberId);
        });
        Assertions.assertEquals(exception.getMessage(),INVALID_EVENT_TIME.formatted(event.getDailyIssueStartTime(), event.getDailyIssueEndTime()));
    }

    @Test
    @DisplayName("쿠폰 발급 - 쿠폰 발급을 기록한다")
    void issue_6() {
        // given
        LocalDateTime currentDateTime = LocalDateTime.of(2024, 02, 27, 13, 0, 0);
        long memberId = 1L;
        long eventId = 1L;
        long couponId = 1L;
        // when
        asyncCouponIssueService.issueCoupon(currentDateTime, eventId, couponId, memberId);
        // then
        Boolean isSaved = redisTemplate.opsForSet().isMember(getIssueRequestKey(couponId), String.valueOf(memberId));
        Assertions.assertTrue(isSaved);
    }

    @Test
    @DisplayName("쿠폰 발급 - 쿠폰 발급 요청이 성공하면 쿠폰 발급 큐에 적재된다")
    void issue_7() throws JsonProcessingException {
        // given
        LocalDateTime currentDateTime = LocalDateTime.of(2024, 02, 27, 13, 0, 0);
        long memberId = 1L;
        long eventId = 1L;
        long couponId = 1L;
        CouponIssueRequestForQueue request = new CouponIssueRequestForQueue(couponId, memberId);
        // when
        asyncCouponIssueService.issueCoupon(currentDateTime, eventId, couponId, memberId);
        // then
        String saveIssueRequest = redisTemplate.opsForList().leftPop(getIssueRequestQueueKey());
        Assertions.assertEquals(new ObjectMapper().writeValueAsString(request), saveIssueRequest);
    }

}