package com.flab.offcoupon.service.coupon_issue.async;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.offcoupon.domain.entity.Coupon;
import com.flab.offcoupon.domain.entity.CouponType;
import com.flab.offcoupon.domain.entity.DiscountType;
import com.flab.offcoupon.domain.entity.Event;
import com.flab.offcoupon.dto.request.CouponIssueRequestForQueue;
import com.flab.offcoupon.exception.coupon.CouponNotFoundException;
import com.flab.offcoupon.exception.coupon.CouponQuantityException;
import com.flab.offcoupon.exception.coupon.DuplicatedCouponException;
import com.flab.offcoupon.exception.event.EventNotFoundException;
import com.flab.offcoupon.exception.event.EventPeriodException;
import com.flab.offcoupon.exception.event.EventTimeException;
import com.flab.offcoupon.repository.mysql.CouponRepository;
import com.flab.offcoupon.repository.mysql.EventRepository;
import org.junit.jupiter.api.Assertions;
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
import java.util.stream.LongStream;

import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.*;
import static com.flab.offcoupon.exception.event.EventErrorMessage.*;
import static com.flab.offcoupon.util.CouponRedisUtils.getIssueRequestKey;
import static com.flab.offcoupon.util.CouponRedisUtils.getIssueRequestQueueKey;

@Transactional
@SpringBootTest
class AsyncDefaultCouponIssueServiceTest {

    @Autowired
    AsyncCouponIssueService asyncCouponIssueService;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    CouponRepository couponRepository;

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
    @DisplayName("[ERROR] 쿠폰 발급 - 쿠폰이 존재하지 않는다면 예외를 반환한다")
    void test() {
        // given
        LocalDateTime currentDateTime = LocalDateTime.now().withHour(13).withMinute(0).withSecond(0);
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
    @DisplayName("[ERROR] 쿠폰 발급 - 쿠폰 발급 수량이 존재하지 않는다면 예외를 반환한다")
    void issueCoupon_fail_with_run_out_of_coupon() {
        // given
        LocalDateTime currentDateTime = LocalDateTime.now().withHour(13).withMinute(0).withSecond(0);
        long memberId = 1000L;
        long couponId = 1L;
        Coupon coupon = couponRepository.findCouponById(couponId)
                .orElseThrow(() -> new CouponNotFoundException(COUPON_NOT_EXIST.formatted(couponId)));;

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
    @DisplayName("[ERROR] 쿠폰 발급 - 이미 발급된 유저라면 예외를 반환한다")
    void issueCoupon_fail_with_duplicated_user() {
        // given
        LocalDateTime currentDateTime = LocalDateTime.now().withHour(13).withMinute(0).withSecond(0);
        long memberId = 1L;
        long couponId = 1L;
        Coupon coupon = couponRepository.findCouponById(couponId)
                .orElseThrow(() -> new CouponNotFoundException(COUPON_NOT_EXIST.formatted(couponId)));
        redisTemplate.opsForSet().add(getIssueRequestKey(coupon.getId()), String.valueOf(memberId));
        // when & then
        DuplicatedCouponException exception = Assertions.assertThrows(DuplicatedCouponException.class, () -> {
            asyncCouponIssueService.issueCoupon(currentDateTime, 1L, coupon.getId(), memberId);
        });
        Assertions.assertEquals(exception.getMessage(),ASYNC_DUPLICATED_COUPON.formatted(memberId, coupon.getId()));
    }

    @Test
    @DisplayName("[ERROR] 쿠폰발급 - 발급 기간이 일치하지 않는다면 예외를 반환한다")
    void issueCoupon_fail_with_invalid_period() {
        // given
        LocalDateTime currentDateTime = LocalDateTime.now().minusDays(1L).withHour(13).withMinute(0).withSecond(0);
        long memberId = 1L;
        long eventId = 1L;
        long couponId = 1L;
        Event event = eventRepository.findEventById(eventId).orElseThrow(() -> new EventNotFoundException(EVENT_NOT_EXIST.formatted(eventId)));
        // when & then
        EventPeriodException exception = Assertions.assertThrows(EventPeriodException.class, () -> {
            asyncCouponIssueService.issueCoupon(currentDateTime, eventId, couponId, memberId);
        });
        Assertions.assertEquals(exception.getMessage(),INVALID_EVENT_PERIOD.formatted(event.getStartDate(), event.getEndDate()));
    }

    @Test
    @DisplayName("[ERROR] 쿠폰발급 - 발급 시간이 일치하지 않는다면 예외를 반환한다")
    void issueCoupon_fail_with_invalid_time() {
        // given
        LocalDateTime currentDateTime = LocalDateTime.now().withHour(10).withMinute(0).withSecond(0);
        long memberId = 1L;
        long eventId = 1L;
        long couponId = 1L;
        Event event = eventRepository.findEventById(eventId).orElseThrow(() -> new EventNotFoundException(EVENT_NOT_EXIST.formatted(eventId)));
        // when & then
        EventTimeException exception = Assertions.assertThrows(EventTimeException.class, () -> {
            asyncCouponIssueService.issueCoupon(currentDateTime, eventId, couponId, memberId);
        });
        Assertions.assertEquals(exception.getMessage(),INVALID_EVENT_TIME.formatted(event.getDailyIssueStartTime(), event.getDailyIssueEndTime()));
    }

    @Test
    @DisplayName("[SUCCESS] 쿠폰 발급 - 쿠폰 발급을 기록한다")
    void issueCoupon_success_and_redis_history() {
        // given
        LocalDateTime currentDateTime = LocalDateTime.now().withHour(13).withMinute(0).withSecond(0);
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
    @DisplayName("[SUCCESS] 쿠폰 발급 - 쿠폰 발급 요청이 성공하면 쿠폰 발급 큐에 적재된다")
    void issueCoupon_success_and_redis_queue() throws JsonProcessingException {
        // given
        LocalDateTime currentDateTime = LocalDateTime.now().withHour(13).withMinute(0).withSecond(0);
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