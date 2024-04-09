package com.flab.offcoupon.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 상품 정보를 담는 도메인 객체입니다.
 */
@ToString
@Getter
@AllArgsConstructor
public final class Product {
    private long id;
    private final String category;
    private final String title;
    private final String description;
    private final long originalPrice; // 원래 가격
    private final Long salePrice; // null일 경우 전체 할인이 적용되지 않은 것으로 간주
    private final long minOrderPrice; // 최소 주문 가격
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
