package com.flab.offcoupon.service.coupon_issue;

import com.flab.offcoupon.repository.mysql.CouponRepository;
import com.flab.offcoupon.repository.mysql.EventRepository;
import com.flab.offcoupon.setup.SetupInitializer;
import com.flab.offcoupon.util.ResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class CouponIssueRequestServiceTest {
    private final static String COUPON_ISSUE_SUCCESS_MESSAGE_SYNC = "쿠폰이 발급 완료되었습니다. memberId : %s, couponId : %s";
    private final static String COUPON_ISSUE_SUCCESS_MESSAGE_ASYNC = "쿠폰이 발급 요청되었습니다. memberId : %s, couponId : %s";
    @Autowired
    private CouponIssueRequestService couponIssueRequestService;

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
        assertEquals(COUPON_ISSUE_SUCCESS_MESSAGE_SYNC.formatted(memberId, couponId), response.getData());
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
        assertEquals(COUPON_ISSUE_SUCCESS_MESSAGE_ASYNC.formatted(memberId, couponId), response.getData());
    }
}