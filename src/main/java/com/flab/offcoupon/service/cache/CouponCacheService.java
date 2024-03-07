package com.flab.offcoupon.service.cache;

import com.flab.offcoupon.domain.entity.Coupon;
import com.flab.offcoupon.domain.entity.Event;
import com.flab.offcoupon.domain.redis.CouponRedisEntity;
import com.flab.offcoupon.domain.redis.EventRedisEntity;
import com.flab.offcoupon.exception.coupon.CouponNotFoundException;
import com.flab.offcoupon.exception.event.EventNotFoundException;
import com.flab.offcoupon.repository.mysql.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.COUPON_NOT_EXIST;

/**
 * 쿠폰 캐시 서비스를 제공하는 클래스입니다.
 */
@RequiredArgsConstructor
@Service
public class CouponCacheService {

    private final CouponRepository couponRepository;

    /**
     * @Cacheable 어노테이션은 쿠폰 ID를 기반으로 쿠폰 정보에 대한 캐시를 생성합니다.
     * 쿠폰 캐시에 데이터가 있다면 조회하고, 없을 경우 데이터베이스에서 가져와서 캐시에 저장합니다.
     *
     * @param couponId 조회할 쿠폰의 ID
     * @return 쿠폰 정보를 담은 CouponRedisEntity 객체
     */
    @Cacheable(key = "#couponId",value = "coupon")
    public CouponRedisEntity getCoupon(long couponId) {
        Coupon coupon = findCoupon(couponId);
        return new CouponRedisEntity(coupon);
    }

    /**
     * 쿠폰 ID를 기반으로 데이터베이스에서 쿠폰 정보를 조회하는 메서드입니다.
     *
     * @param couponId 조회할 쿠폰의 ID
     * @return 조회된 쿠폰 객체
     * @throws CouponNotFoundException 쿠폰이 존재하지 않을 경우 발생하는 예외
     */
    @Transactional(readOnly = true)
    public Coupon findCoupon(long couponId) {
        return couponRepository.findCouponById(couponId)
                .orElseThrow(() -> new CouponNotFoundException(COUPON_NOT_EXIST.formatted(couponId)));
    }
}
