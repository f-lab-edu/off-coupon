package com.flab.offcoupon.domain.entity;

import com.flab.offcoupon.domain.entity.helper.AppliedCouponInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderCouponTest {

    @DisplayName("OrderCoupon 생성")
    @Test
    void createOrderCoupon() {
        // Given
        OrderCoupon orderCoupon = OrderCoupon.createOrderCoupon(1L, new AppliedCouponInfo(1L, BigDecimal.valueOf(10000)));
        assertNotNull(orderCoupon);
    }
}