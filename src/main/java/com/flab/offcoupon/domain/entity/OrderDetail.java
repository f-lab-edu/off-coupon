package com.flab.offcoupon.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.flab.offcoupon.domain.entity.helper.OrderInfo;
import com.flab.offcoupon.domain.entity.params.TimeParams;
import com.flab.offcoupon.model.PositiveBigDecimal;
import com.flab.offcoupon.model.PositiveLong;
import com.flab.offcoupon.util.DateTimeUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

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
		this.productId = new PositiveLong(orderInfo.getProductId()).getValue();
		this.quantity = new PositiveLong(orderInfo.getQuantity()).getValue();
		this.pricePerEach = new PositiveBigDecimal(orderInfo.getPricePerEach()).getValue();
		this.totalOrderPrice = new PositiveBigDecimal(orderInfo.getTotalOrderPrice()).getValue();
		this.totalDiscountPrice = new PositiveBigDecimal(orderInfo.getTotalDiscountPrice()).getValue();
		this.totalPaymentPrice = new PositiveBigDecimal(orderInfo.getTotalPaymentPrice()).getValue();
		this.createdAt = timeParams.createdAt();
		this.updatedAt = timeParams.updatedAt();
	}

	public static OrderDetail createOrderDetail(OrderInfo orderInfo) {
		LocalDateTime now = DateTimeUtils.nowFromZone();
		return new OrderDetail(orderInfo, new TimeParams(now, now));
	}
}
