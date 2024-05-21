package com.flab.offcoupon.domain.entity;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 상품 정보를 담는 도메인 객체입니다.
 */
@ToString
@Getter
public final class Product {
    private long id;
    private final String category;
    private final String title;
    private final String description;
    private final BigDecimal originalPrice; // 원래 가격
    private final BigDecimal salePrice; // 0일 경우 전체 할인이 적용되지 않은 것으로 간주
    private final BigDecimal minOrderPrice; // 최소 주문 가격
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Product(long id, String category, String title, String description, BigDecimal originalPrice, BigDecimal salePrice, BigDecimal minOrderPrice, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.description = description;
        this.originalPrice = originalPrice;
        this.salePrice = salePrice;
        this.minOrderPrice = minOrderPrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 상품의 개별 가격을 반환하는 메소드입니다.<br>
     * 만약 세일 가격이 0일 경우 원래 가격을 반환합니다.
     *
     * @return 상품의 개별 가격
     */
        public BigDecimal getPricePerUnit() {
        return this.salePrice.compareTo(BigDecimal.ZERO) > 0 ? salePrice : originalPrice;
    }

    /**
     * 상품의 총 가격을 계산하여 반환하는 메소드입니다.
     *
     * @param quantity 상품 수량
     * @return 상품의 총 가격
     */
    public BigDecimal calculateTotalPrice(long quantity) {
        return getPricePerUnit().multiply(BigDecimal.valueOf(quantity));
    }


    /**
     * 상품에 대한 총 할인 가격을 계산하여 반환하는 메소드입니다.
     *
     * @param couponList  쿠폰 목록
     * @param quantity    상품 수량
     * @return 총 할인 가격
     */

    public BigDecimal calculateTotalDiscountPrice(List<Coupon> couponList, long quantity) {
        BigDecimal totalDiscountPrice = BigDecimal.ZERO;
        for (Coupon coupon : couponList) {
            if (coupon.getDiscountType() == DiscountType.PERCENT) {
                BigDecimal discountRate = BigDecimal.valueOf(coupon.getDiscountRate()).divide(BigDecimal.valueOf(100));
                BigDecimal discountAmount = getPricePerUnit().multiply(discountRate);
                totalDiscountPrice = totalDiscountPrice.add(discountAmount);
            } else {
                totalDiscountPrice = totalDiscountPrice.add(BigDecimal.valueOf(coupon.getDiscountPrice()));
            }
        }
        return totalDiscountPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 상품의 개당 할인 가격을 계산하여 반환하는 메소드입니다.
     *
     * @param coupon  쿠폰 정보
     * @return 개당 할인 가격
     */
    public BigDecimal calculateDiscountPricePerUnit(Coupon coupon) {
        BigDecimal discountPricePerUnit;
        if (coupon.getDiscountType() == DiscountType.PERCENT) {
            BigDecimal discountRate = BigDecimal.valueOf(coupon.getDiscountRate()).divide(BigDecimal.valueOf(100));
            discountPricePerUnit = getPricePerUnit().multiply(discountRate);
        } else {
            discountPricePerUnit = BigDecimal.valueOf(coupon.getDiscountPrice());
        }

        return discountPricePerUnit;
    }


    /**
     * 상품에 대한 총 결제 가격을 계산하여 반환하는 메소드입니다.<br>
     * 총 결제 가격 = (총 주문 가격 -  할인 가격)
     * @param quantity    상품 수량
     * @param couponList  쿠폰 목록
     * @return 총 결제 가격
     */
    public BigDecimal calculateTotalPaymentPrice(long quantity, List<Coupon> couponList) {
        BigDecimal totalPrice = calculateTotalPrice(quantity);
        BigDecimal totalDiscountPrice = calculateTotalDiscountPrice(couponList, quantity);
        return totalPrice.subtract(totalDiscountPrice);
    }
}
