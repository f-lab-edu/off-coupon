package com.flab.offcoupon.service.coupon_issue.async;

import com.flab.offcoupon.domain.entity.Coupon;
import com.flab.offcoupon.domain.entity.Event;
import com.flab.offcoupon.exception.coupon.CouponNotFoundException;
import com.flab.offcoupon.exception.coupon.CouponQuantityException;
import com.flab.offcoupon.exception.coupon.DuplicatedCouponException;
import com.flab.offcoupon.exception.event.EventNotFoundException;
import com.flab.offcoupon.exception.event.EventPeriodException;
import com.flab.offcoupon.exception.event.EventTimeException;
import com.flab.offcoupon.repository.mysql.CouponRepository;
import com.flab.offcoupon.repository.mysql.EventRepository;
import com.flab.offcoupon.setup.SetupInitializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.LongStream;

import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.*;
import static com.flab.offcoupon.exception.event.EventErrorMessage.*;
import static com.flab.offcoupon.util.CouponRedisUtils.getIssueRequestKey;

@SpringBootTest
@Transactional
class AsyncDefaultCouponIssueServiceTest {

    @Autowired
    private AsyncCouponIssueService asyncCouponIssueService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private SetupInitializer setupInitializer;

    @BeforeEach
    void clear() {
        Collection<String> redisKeys = redisTemplate.keys("*");
        redisTemplate.delete(redisKeys);
    }
    @BeforeEach
    void setUp() {
        setupInitializer = new SetupInitializer(eventRepository, couponRepository);
        setupInitializer.setUpEventAndCoupon();
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
}