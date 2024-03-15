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

@RequiredArgsConstructor
@Service
public class CouponIssueRedisService {

    private final RedisRepository redisRepository;

    public void checkCouponIssueQuantityAndDuplicate(CouponRedisEntity coupon, long memberId) {
        // 쿠폰 발급 수량 검증
        if (!availableTotalIssueQuantity(coupon.maxQuantity(), coupon.id())) {
            throw new CouponQuantityException(ASYNC_INVALID_COUPON_QUANTITY.formatted(coupon.id()));
        }
        // 중복 발급 요청 검증
        if (!availableUserIssueQuantity(coupon.id(), memberId)) {
            throw new DuplicatedCouponException(ASYNC_DUPLICATED_COUPON.formatted(memberId, coupon.id()));
        }
    }

    // 수량 검증 (해당 유저가 선착순 조건에 만족했는지, totalquantity관점에서 검증)
    public boolean availableTotalIssueQuantity(Long maxQuantity, long couponId) {
        if (maxQuantity == null) {
            return true;
        }
        String key = getIssueRequestKey(couponId);
        // set의 사이즈가 maxQuantity보다 작을때 true 반환
        return maxQuantity > redisRepository.sCard(key);
    }
    // 중복 검증
    public boolean availableUserIssueQuantity(long couponId, long memberId) {
        String key = getIssueRequestKey(couponId);
        // set에 존재하지 않는 대상만 true 반환
        return !redisRepository.sIsMember(key, String.valueOf(memberId));
    }
}
