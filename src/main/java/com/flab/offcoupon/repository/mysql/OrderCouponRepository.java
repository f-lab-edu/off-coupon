package com.flab.offcoupon.repository.mysql;

import com.flab.offcoupon.domain.entity.OrderCoupon;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderCouponRepository {
    void save(OrderCoupon orderCoupon);
}
