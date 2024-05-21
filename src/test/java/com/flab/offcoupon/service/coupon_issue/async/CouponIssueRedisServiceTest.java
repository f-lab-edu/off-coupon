package com.flab.offcoupon.service.coupon_issue.async;

import com.flab.offcoupon.repository.redis.RedisRepository;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

import static com.flab.offcoupon.util.RedisKeyUtils.getIssueRequestKey;
@SpringBootTest
@Transactional
class CouponIssueRedisServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(AsyncCouponIssueServiceTest.class);

    @Autowired
    CouponIssueRedisService couponIssueRedisService;

    @Autowired
    private RedisRepository redisRepository;

    @BeforeEach
    void setUp() {
        // 테스트 컨테이너에 연결되었는지 확인
        logger.info("spring.redis.host : " + System.getProperty("spring.redis.host"));
        logger.info("spring.redis.port : " + System.getProperty("spring.redis.port"));
        logger.info("spring.redis.password : " + System.getProperty("spring.redis.password"));
    }

    @AfterEach
    void clear() {
        // 테스트 종료 후 발급 요청 키와 쿠폰, 이벤트 데이터 삭제
        redisRepository.delete(getIssueRequestKey(1L));
        redisRepository.delete("coupon::1");
        redisRepository.delete("event::1");
    }

    @Test
    @DisplayName("[SUCCESS] 쿠폰 수량 검증 - 발급 가능 수량이 존재하면 true를 반환한다")
    void availableTotalIssueQuantity_success() {
        // given
        long totalQuantity = 10L;
        long couponId = 1L;
        // when
        boolean actual = couponIssueRedisService.availableTotalIssueQuantity(totalQuantity, couponId);
        // then
        Assertions.assertTrue(actual);
    }

    @Test
    @DisplayName("[ERROR] 쿠폰 수량 검증 - 발급 가능 수량이 모두 소진되면 false를 반환한다")
    void availableTotalIssueQuantity_run_out_of_coupon() {
        // given
        long totalQuantity = 10L;
        long couponId = 1L;
        // totalQuantity 만큼 memberId를 발급 요청에 추가
        IntStream.range(0, 10).forEach(memberId -> {
            redisRepository.sAdd(getIssueRequestKey(couponId), String.valueOf(memberId));
        });
        // when
        boolean actual = couponIssueRedisService.availableTotalIssueQuantity(totalQuantity, couponId);
        // then
        Assertions.assertFalse(actual);
    }

    @Test
    @DisplayName("[SUCCESS] 쿠폰 중복 발급 검증 - 발급된 내역에 유저가 존재하지 않으면 true를 반환한다")
    void availableUserIssueQuantity_not_duplicate_request() {
        // given
        long couponId = 1L;
        long memberId = 1L;
        // when
        boolean actual = couponIssueRedisService.availableUserIssueQuantity(couponId, memberId);
        // then
        Assertions.assertTrue(actual);
    }

    @Test
    @DisplayName("[ERROR] 쿠폰 중복 발급 검증 - 발급된 내역에 유저가 존재하면 false를 반환한다")
    void availableUserIssueQuantity_duplicate_request() {
        // given
        long couponId = 1L;
        long memberId = 1L;
        redisRepository.sAdd(getIssueRequestKey(couponId), String.valueOf(memberId));

        // when
        boolean actual = couponIssueRedisService.availableUserIssueQuantity(couponId, memberId);
        // then
        Assertions.assertFalse(actual);
    }

}