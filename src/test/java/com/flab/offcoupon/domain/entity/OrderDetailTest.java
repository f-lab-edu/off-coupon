package com.flab.offcoupon.domain.entity;

import com.flab.offcoupon.domain.entity.helper.OrderInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderDetailTest {

    @DisplayName("OrderDetail 생성")
    @Test
    void createOrderDetail() {
        // Given
        OrderInfo orderInfo = new OrderInfo(1L, 2L, BigDecimal.valueOf(10000L), BigDecimal.valueOf(10000L),
                null, BigDecimal.valueOf(3000), BigDecimal.valueOf(7000));
        OrderDetail orderCoupon = OrderDetail.createOrderDetail(orderInfo);
        assertNotNull(orderCoupon);
    }
}