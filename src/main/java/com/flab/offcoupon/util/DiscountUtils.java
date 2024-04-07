package com.flab.offcoupon.util;

import com.flab.offcoupon.domain.entity.DiscountType;
import lombok.experimental.UtilityClass;

/**
 * 할인 정보를 반환하는 유틸리티 클래스입니다.
 */
@UtilityClass
public class DiscountUtils {
    /**
     * 할인 정보를 반환하는 메소드입니다.<br>
     *
     * String 더하기 연산은 성능이 좋지 않을 수 있으므로 StringBuilder를 사용하여 최적화하였습니다.
     * @param discountType 할인 종류 (DiscountType enum 값)
     * @param discountRate 할인율 (percent 할인의 경우)
     * @param discountPrice 할인액 (amount 할인의 경우)
     * @return 할인 정보를 나타내는 문자열
     */
    public static String getDiscount(DiscountType discountType, Long discountRate, Long discountPrice) {
        StringBuilder sb = new StringBuilder();
        if (discountType == DiscountType.AMOUNT) {
            return sb.append(discountPrice).append("원 할인").toString();
        } else {
            return sb.append(discountRate).append("% 할인").toString();
        }
    }
}
