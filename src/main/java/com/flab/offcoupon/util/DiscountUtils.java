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

    public long pricePerEach(Product product) {
        return product.getSalePrice() == null ? product.getOriginalPrice().longValue() : product.getSalePrice().longValue();
    }

    public long totalOrderPrice(Product product, long quantity) {
        return pricePerEach(product) * quantity;
    }

    public long totalDiscountPrice(Product product, List<Coupon> couponList, long quantity) {
        long totalDiscountPrice = 0;
        for (Coupon coupon : couponList) {
            if (coupon.getDiscountType() == DiscountType.PERCENT) {
                double test = pricePerEach(product) * ((double)coupon.getDiscountRate() / 100);
                System.out.println("pricePerEach: " + pricePerEach(product));
                System.out.println("coupon.getDiscountRate() : " + coupon.getDiscountRate());
                System.out.println("(coupon.getDiscountRate() / 100 : " + (coupon.getDiscountRate() / 100));
                System.out.println("test: " + test);
                totalDiscountPrice += test;
            } else {
                totalDiscountPrice += coupon.getDiscountPrice();
            }
        }
        return totalDiscountPrice * quantity;
    }

    public long calculateEachDiscountPrice(Product product, List<Coupon> couponList) {
        long totalDiscountPrice = 0;
        for (Coupon coupon : couponList) {
            if (coupon.getDiscountType() == DiscountType.PERCENT) {
                totalDiscountPrice += pricePerEach(product) * (coupon.getDiscountRate() / 100);
            } else {
                totalDiscountPrice += coupon.getDiscountPrice();
            }
        }
        return totalDiscountPrice;
    }

    public long calculateEachDiscountPrice(Product product, Coupon coupon) {
        long totalDiscountPrice = 0;

        if (coupon.getDiscountType() == DiscountType.PERCENT) {
            totalDiscountPrice += pricePerEach(product) * coupon.getDiscountRate() / 100;
        } else {
            totalDiscountPrice += coupon.getDiscountPrice();
        }

        return totalDiscountPrice;
    }

    public long totalPaymentPrice(Product product, long quantity, List<Coupon> couponList) {
        return totalOrderPrice(product, quantity) - totalDiscountPrice(product, couponList, quantity);
    }
}
