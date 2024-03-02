package com.flab.offcoupon.repository;

import com.flab.offcoupon.domain.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.Optional;

@Mapper
public interface CouponRepository {
    Optional<Coupon> findCouponById(long couponId);
    Optional<Coupon> findCouponByIdPessimisticLock(long couponId);
    Optional<Coupon> findCouponByIdWithOptimisticLock(long couponId);
    void increaseIssuedQuantity(Coupon coupon);
    int increaseIssuedQuantityWithOptimisticLock(@Param("originVersion") BigInteger originVersion,
                                                  @Param("coupon") Coupon coupon);
}
