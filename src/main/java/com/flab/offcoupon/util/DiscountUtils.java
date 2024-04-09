package com.flab.offcoupon.util;

import com.flab.offcoupon.domain.entity.Coupon;
import com.flab.offcoupon.domain.entity.DiscountType;
import com.flab.offcoupon.domain.entity.Product;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * 할인 정보를 반환하는 유틸리티 클래스입니다.
 */
@UtilityClass
public class DiscountUtils {
    /**
     * 할인 정보를 반환하는 메소드입니다.<br>
     * <p>
     * String 더하기 연산은 성능이 좋지 않을 수 있으므로 StringBuilder를 사용하여 최적화하였습니다.
     *
     * @param discountType  할인 종류 (DiscountType enum 값)
     * @param discountRate  할인율 (percent 할인의 경우)
     * @param discountPrice 할인액 (amount 할인의 경우)
     * @return 할인 정보를 나타내는 문자열
     */
    public String getDiscount(DiscountType discountType, Long discountRate, Long discountPrice) {
        StringBuilder sb = new StringBuilder();
        if (discountType == DiscountType.AMOUNT) {
            return sb.append(discountPrice).append("원 할인").toString();
        } else {
            return sb.append(discountRate).append("% 할인").toString();
        }
    }

    /**
     * 상품의 개별 가격을 반환하는 메소드입니다.<br>
     * 만약 세일 가격이 없다면 원래 가격을 반환합니다.
     *
     * @param product 상품 정보
     * @return 상품의 개별 가격
     */
    public long getProductPricePerUnit(Product product) {
        return product.getSalePrice() == null ? product.getOriginalPrice() : product.getSalePrice();
    }

    /**
     * 상품의 총 가격을 계산하여 반환하는 메소드입니다.
     *
     * @param product  상품 정보
     * @param quantity 상품 수량
     * @return 상품의 총 가격
     */
    public long calculateTotalPrice(Product product, long quantity) {
        return getProductPricePerUnit(product) * quantity;
    }

    /**
     * 상품에 대한 총 할인 가격을 계산하여 반환하는 메소드입니다.
     *
     * @param product     상품 정보
     * @param couponList  쿠폰 목록
     * @param quantity    상품 수량
     * @return 총 할인 가격
     */
    public long calculateTotalDiscountPrice(Product product, List<Coupon> couponList, long quantity) {
        long totalDiscountPrice = 0;
        for (Coupon coupon : couponList) {
            if (coupon.getDiscountType() == DiscountType.PERCENT) {
                totalDiscountPrice += getProductPricePerUnit(product) * ((double) coupon.getDiscountRate() / 100);
            } else {
                totalDiscountPrice += coupon.getDiscountPrice();
            }
        }
        return totalDiscountPrice * quantity;
    }

    /**
     * 상품의 개당 할인 가격을 계산하여 반환하는 메소드입니다.
     *
     * @param product 상품 정보
     * @param coupon  쿠폰 정보
     * @return 개당 할인 가격
     */
    public long calculateDiscountPricePerUnit(Product product, Coupon coupon) {
        long discountPricePerUnit = 0;

        if (coupon.getDiscountType() == DiscountType.PERCENT) {
            discountPricePerUnit += getProductPricePerUnit(product) * coupon.getDiscountRate() / 100;
        } else {
            discountPricePerUnit += coupon.getDiscountPrice();
        }

        return discountPricePerUnit;
    }


    /**
     * 상품에 대한 총 결제 가격을 계산하여 반환하는 메소드입니다.
     *
     * @param product     상품 정보
     * @param quantity    상품 수량
     * @param couponList  쿠폰 목록
     * @return 총 결제 가격
     */
    public long calculateTotalPaymentPrice(Product product, long quantity, List<Coupon> couponList) {
        return calculateTotalPrice(product, quantity) - calculateTotalDiscountPrice(product, couponList, quantity);
    }
}
