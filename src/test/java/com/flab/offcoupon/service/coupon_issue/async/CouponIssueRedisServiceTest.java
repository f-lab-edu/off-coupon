package com.flab.offcoupon.service.coupon_issue.async;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.stream.IntStream;

import static com.flab.offcoupon.util.CouponRedisUtils.getIssueRequestKey;

@SpringBootTest
class CouponIssueRedisServiceTest {

    @Autowired
    CouponIssueRedisService couponIssueRedisService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void clear() {
        Collection<String> redisKeys = redisTemplate.keys("*");
        redisTemplate.delete(redisKeys);
    }

    @Test
    @DisplayName("쿠폰 수량 검증 - 발급 가능 수량이 존재하면 true를 반환한다")
    void availableTotalIssueQuantity_1() {
        // given
        long totalQuantity = 10L;
        long couponId = 1L;

        // when
        boolean actual = couponIssueRedisService.availableTotalIssueQuantity(totalQuantity, couponId);
        // then
        Assertions.assertTrue(actual);
    }

    @Test
    @DisplayName("쿠폰 수량 검증 - 발급 가능 수량이 모두 소진되면 false를 반환한다")
    void availableTotalIssueQuantity_2() {
        // given
        long totalQuantity = 10L;
        long couponId = 1L;
        IntStream.range(0, 10).forEach(userId -> {
            redisTemplate.opsForSet().add(getIssueRequestKey(couponId), String.valueOf(userId));
        });
        // when
        boolean actual = couponIssueRedisService.availableTotalIssueQuantity(totalQuantity, couponId);
        // then
        Assertions.assertFalse(actual);
    }

    @Test
    @DisplayName("쿠폰 중복 발급 검증 - 발급된 내역에 유저가 존재하지 않으면 true를 반환한다")
    void availableUserIssueQuantity_1() {
        // given
        long couponId = 1L;
        long memberId = 1L;

        // when
        boolean actual = couponIssueRedisService.availableUserIssueQuantity(couponId, memberId);
        Assertions.assertTrue(actual);
    }

    @Test
    @DisplayName("쿠폰 중복 발급 검증 - 발급된 내역에 유저가 존재하면 false를 반환한다")
    void availableUserIssueQuantity_2() {
        // given
        long couponId = 1L;
        long memberId = 1L;
        redisTemplate.opsForSet().add(getIssueRequestKey(couponId), String.valueOf(memberId));

        // when
        boolean actual = couponIssueRedisService.availableUserIssueQuantity(couponId, memberId);
        Assertions.assertFalse(actual);
    }

}