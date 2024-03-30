package com.flab.offcoupon.repository.mysql;

import com.flab.offcoupon.domain.entity.Coupon;
import com.flab.offcoupon.domain.vo.persistence.couponissue.UpdateTotalIssuedQuantityVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface CouponRepository {
    void save(Coupon coupon);
    Optional<Coupon> findCouponById(long couponId);
    Optional<Coupon> findCouponByIdPessimisticLock(long couponId);
    void increaseIssuedQuantity(Coupon coupon);
    void updateTotalIssuedCouponQuantity(UpdateTotalIssuedQuantityVo updateTotalIssuedQuantityVo);
}
