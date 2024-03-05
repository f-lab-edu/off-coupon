package com.flab.offcoupon.service;

import com.flab.offcoupon.domain.entity.Coupon;
import com.flab.offcoupon.exception.coupon.CouponNotFoundException;
import com.flab.offcoupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.COUPON_NOT_EXIST;

@Slf4j
@RequiredArgsConstructor
@Service
public class IncreaseIssuedCoupon {

    private final CouponRepository couponRepository;
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    public void increaseIssuedCouponQuantity(long couponId) {
        log.info("트랜잭션 2 쿠폰 발급 수 증가. couponId : {}", couponId);
        Coupon existingCoupon = findCoupon(couponId);
        Coupon updatecoupon = existingCoupon.increaseIssuedQuantity(existingCoupon);
        couponRepository.increaseIssuedQuantity(updatecoupon);
    }
    @Transactional(readOnly = true)
    public Coupon findCoupon(long couponId) {
        log.info("트랜잭션 2 쿠폰 조회. couponId : {}", couponId);
        return couponRepository.findCouponById(couponId)
                .orElseThrow(() -> new CouponNotFoundException(COUPON_NOT_EXIST.formatted(couponId)));
    }
}
