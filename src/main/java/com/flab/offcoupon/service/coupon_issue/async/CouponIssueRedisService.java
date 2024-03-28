package com.flab.offcoupon.service.coupon_issue.async;

import com.flab.offcoupon.domain.redis.CouponRedisEntity;
import com.flab.offcoupon.exception.coupon.CouponQuantityException;
import com.flab.offcoupon.exception.coupon.DuplicatedCouponException;
import com.flab.offcoupon.repository.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.ASYNC_DUPLICATED_COUPON;
import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.ASYNC_INVALID_COUPON_QUANTITY;
import static com.flab.offcoupon.util.CouponRedisUtils.getIssueRequestKey;

/**
 * 비동기 쿠폰 발급 시 Redis를 사용하여 수량 및 중복 여부를 확인하는 서비스 클래스입니다.
 */
@RequiredArgsConstructor
@Service
public class CouponIssueRedisService {

    private final RedisRepository redisRepository;
    /**
     * 쿠폰 발급 가능 여부 및 중복 발급 여부를 확인합니다.
     *
     * @param coupon   발급할 쿠폰 정보
     * @param memberId 발급 요청을 하는 회원의 ID
     * @throws CouponQuantityException    발급 가능 수량 초과 시 발생하는 예외
     * @throws DuplicatedCouponException  중복 발급 요청 시 발생하는 예외
     */
    public void checkCouponIssueQuantityAndDuplicate(CouponRedisEntity coupon, long memberId) {
        if (!availableTotalIssueQuantity(coupon.maxQuantity(), coupon.id())) {
            throw new CouponQuantityException(ASYNC_INVALID_COUPON_QUANTITY.formatted(coupon.id()));
        }
        if (!availableUserIssueQuantity(coupon.id(), memberId)) {
            throw new DuplicatedCouponException(ASYNC_DUPLICATED_COUPON.formatted(memberId, coupon.id()));
        }
    }
    /**
     * Set 데이터 구조를 활용하여 총 발급 가능 수량과 유저가 선착순 조건에 만족했는지 검증합니다.
     *
     * @param maxQuantity 발급 가능한 최대 수량
     * @param couponId    쿠폰 ID
     * @return 발급 가능 여부
     */
    public boolean availableTotalIssueQuantity(Long maxQuantity, long couponId) {
        if (maxQuantity == null) {
            return true;
        }
        String key = getIssueRequestKey(couponId);
        // set의 사이즈가 maxQuantity보다 작을때 true 반환
        return maxQuantity > redisRepository.sCard(key);
    }
    /**
     * Set 데이터 구조를 활용하여 사용자의 중복 발급 여부를 검증합니다.
     *
     * @param couponId  쿠폰 ID
     * @param memberId  회원 ID
     * @return 중복 발급 여부
     */
    public boolean availableUserIssueQuantity(long couponId, long memberId) {
        String key = getIssueRequestKey(couponId);
        // set에 존재하지 않는 대상만 true 반환
        return !redisRepository.sIsMember(key, String.valueOf(memberId));
    }
}
