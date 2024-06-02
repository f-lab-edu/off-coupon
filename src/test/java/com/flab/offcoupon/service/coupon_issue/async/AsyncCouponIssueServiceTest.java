package com.flab.offcoupon.service.coupon_issue.async;

import com.flab.offcoupon.dto.request.IssueRequestParameter;
import com.flab.offcoupon.exception.coupon.CouponNotFoundException;
import com.flab.offcoupon.exception.coupon.CouponQuantityException;
import com.flab.offcoupon.exception.coupon.DuplicatedCouponException;
import com.flab.offcoupon.model.PositiveLong;
import com.flab.offcoupon.repository.mysql.CouponIssueRepository;
import com.flab.offcoupon.repository.mysql.CouponRepository;
import com.flab.offcoupon.repository.mysql.EventRepository;
import com.flab.offcoupon.repository.redis.RedisRepository;
import com.flab.offcoupon.setup.SetupInitializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.LongStream;

import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.*;
import static com.flab.offcoupon.util.RedisKeyUtils.getIssueRequestKey;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;
@SpringBootTest
@Transactional
class AsyncCouponIssueServiceTest {
    @Autowired
    private AsyncCouponIssueService asyncCouponIssueService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private RedisRepository redisRepository;

    @Autowired
    private CouponIssueRepository couponIssueRepository;

    private SetupInitializer setupInitializer;

    @BeforeEach
    void setUp() {
        // issueRequestKey에 memberId를 추가하여 테스트를 위한 초기 데이터 설정
        redisRepository.sAdd(getIssueRequestKey(1L), String.valueOf(1L));
        // 이벤트와 쿠폰 데이터 초기화
        setupInitializer = new SetupInitializer(eventRepository, couponRepository);
        setupInitializer.setUpEventAndCoupon();
    }

    @AfterEach
    void clear() throws Exception {
        redisRepository.delete(getIssueRequestKey(1L));
        redisRepository.delete("coupon::1");
        redisRepository.delete("event::1");
        couponIssueRepository.deleteCouponIssueByMemberIdAndCouponId(2L, 1L);
    }

    @Transactional
    @DisplayName("비동기식 쿠폰 발급")
    @Nested()
    class issueCoupon {

        @Test
        @DisplayName("[ERROR] 쿠폰 식별자가 존재하지 않는다면, CouponNotFoundException 발생")
        void issueCoupon_fail_if_coupon_is_not_exist() {
            // given
            LocalDateTime currentDateTime = LocalDateTime.now().withHour(13).withMinute(0).withSecond(0);
            long eventId = 1L;
            long couponId = 2L;
            long memberId = 1L;
            IssueRequestParameter parameter = new IssueRequestParameter(new PositiveLong(eventId), new PositiveLong(couponId), new PositiveLong(memberId));

            // when & then
            CouponNotFoundException exception = Assertions.assertThrows(CouponNotFoundException.class, () -> {
                asyncCouponIssueService.issueCoupon(currentDateTime, parameter);
            });
            Assertions.assertEquals(exception.getMessage(), COUPON_NOT_EXIST.formatted(couponId));
        }

        @Test
        @DisplayName("[ERROR] 쿠폰 발급 가능 수량이 존재하지 않는다면, 예외를 반환한다")
        void issueCoupon_fail_with_run_out_of_coupon() {
            // given
            LocalDateTime currentDateTime = LocalDateTime.now().withHour(13).withMinute(0).withSecond(0);
            long eventId  = 1L;
            long memberId = 1000L;
            long couponId = 1L;
            // max quantity 갯수만큼 쿠폰 발급 요청을 추가
            LongStream.range(0, 500).forEach(increasingMemberId -> {
                redisRepository.sAdd(getIssueRequestKey(1L), String.valueOf(increasingMemberId));
            });
            IssueRequestParameter parameter = new IssueRequestParameter(new PositiveLong(eventId), new PositiveLong(couponId), new PositiveLong(memberId));

            // when & then
            CouponQuantityException exception = Assertions.assertThrows(CouponQuantityException.class, () -> {
                asyncCouponIssueService.issueCoupon(currentDateTime, parameter);
            });

            Assertions.assertEquals(exception.getMessage(), ASYNC_INVALID_COUPON_QUANTITY.formatted(couponId));
        }

        @Test
        @DisplayName("[ERROR] 쿠폰 발급 - 이미 발급된 유저라면 예외를 반환한다")
        void issueCoupon_fail_with_duplicated_user() {
            // given
            LocalDateTime currentDateTime = LocalDateTime.now().withHour(13).withMinute(0).withSecond(0);
            long eventId = 1L;
            long memberId = 1L;
            long couponId = 1L;
            IssueRequestParameter parameter = new IssueRequestParameter(new PositiveLong(eventId), new PositiveLong(couponId), new PositiveLong(memberId));

            // when & then
            DuplicatedCouponException exception = Assertions.assertThrows(DuplicatedCouponException.class, () -> {
                asyncCouponIssueService.issueCoupon(currentDateTime, parameter);
            });
            Assertions.assertEquals(exception.getMessage(), ASYNC_DUPLICATED_COUPON.formatted(memberId, couponId));
        }

        @Test
        @DisplayName("[SUCCESS] 쿠폰 발급 - 쿠폰 발급을 기록한다")
        void issueCoupon_success_and_redis_history() {
            // given
            LocalDateTime currentDateTime = LocalDateTime.now().withHour(13).withMinute(0).withSecond(0);
            long memberId = 2L;
            long eventId = 1L;
            long couponId = 1L;
            IssueRequestParameter parameter = new IssueRequestParameter(new PositiveLong(eventId), new PositiveLong(couponId), new PositiveLong(memberId));

            // when
            asyncCouponIssueService.issueCoupon(currentDateTime, parameter);
            // then
            await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
                String key = getIssueRequestKey(couponId);
                Boolean isSaved = redisRepository.sIsMember(key, String.valueOf(memberId));
                Assertions.assertTrue(isSaved);
            });
        }
    }
}