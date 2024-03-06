package com.flab.offcoupon.service.couponIssue;

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
public class IncreaseIssuedCouponWithReadCommitted {

    private final CouponRepository couponRepository;
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void increaseIssuedCouponQuantity(long couponId) {
        Coupon existingCoupon = findCoupon(couponId);
        Coupon updatecoupon = existingCoupon.increaseIssuedQuantity(existingCoupon);
        couponRepository.increaseIssuedQuantity(updatecoupon);
    }
    @Transactional(readOnly = true)
    public Coupon findCoupon(long couponId) {
        return couponRepository.findCouponById(couponId)
                .orElseThrow(() -> new CouponNotFoundException(COUPON_NOT_EXIST.formatted(couponId)));
    }
}
