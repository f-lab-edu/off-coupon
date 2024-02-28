package com.flab.offcoupon.repository;

import com.flab.offcoupon.domain.Coupon;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface CouponRepository {
    Optional<Coupon> findCouponById(long couponId);
    void increaseIssuedQuantity(Coupon coupon);
}
