package com.flab.offcoupon.domain.entity;

import com.flab.offcoupon.domain.entity.helper.OrderInfo;
import com.flab.offcoupon.domain.entity.params.TimeParams;
import com.flab.offcoupon.exception.common.InvalidProductPriceException;
import com.flab.offcoupon.model.Positive;
import com.flab.offcoupon.util.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 주문 상세 정보를 담는 도메인 객체입니다.
 */
@ToString
@Getter
@AllArgsConstructor
public final class OrderDetail {
    private long id;
    private final long productId;
    private final long quantity;
    private final BigDecimal pricePerEach; // 상품 1개당 가격
    private final BigDecimal totalOrderPrice; // 총 상품 주문 가격
    private final BigDecimal totalDiscountPrice; // 할인 가격
    private final BigDecimal totalPaymentPrice; // 총 상품 주문 가격 - 할인 가격
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private OrderDetail(OrderInfo orderInfo, TimeParams timeParams) {
        this.productId = orderInfo.getProductId();
        this.quantity = orderInfo.getQuantity();
        this.pricePerEach = orderInfo.getPricePerEach();
        this.totalOrderPrice = orderInfo.getTotalOrderPrice();
        this.totalDiscountPrice = orderInfo.getTotalDiscountPrice();
        this.totalPaymentPrice = orderInfo.getTotalPaymentPrice();
        this.createdAt = timeParams.createdAt();
        this.updatedAt = timeParams.updatedAt();
        validateIdIsNegativeAndPricePerEachIsNegative(productId, quantity, pricePerEach);
    }

    public static OrderDetail createOrderDetail(OrderInfo orderInfo) {
        LocalDateTime now = DateTimeUtils.nowFromZone();
        return new OrderDetail(orderInfo, new TimeParams(now, now));
    }

    private void validateIdIsNegativeAndPricePerEachIsNegative(long productId, long quantity, BigDecimal pricePerEach) {
        Positive positiveProductId = new Positive(productId);
        Positive positiveQuantity = new Positive(quantity);
        if (pricePerEach.compareTo(BigDecimal.ZERO) < 0 || pricePerEach.compareTo(BigDecimal.ZERO) == 0) {
            throw new InvalidProductPriceException();
        }
    }
}
