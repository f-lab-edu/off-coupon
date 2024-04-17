package com.flab.offcoupon.repository.mysql;

import com.flab.offcoupon.domain.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailRepository {
    Long save(OrderDetail orderDetail);
}
