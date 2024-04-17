package com.flab.offcoupon.util;

import com.flab.offcoupon.domain.entity.DiscountType;
import lombok.experimental.UtilityClass;

/**
 * 할인 정보를 반환하는 유틸리티 클래스입니다.
 */
@UtilityClass
public class DiscountUtils {
    /**
     * 할인 정보를 문자열로 반환하는 메소드입니다.<br>
     * <p>
     * JDK 5이상 부터 반복문이 아닌 곳에서 String 덧셈 연산을 할 경우
     * 컴파일 최적화로 인해 해당 연산을 StringBuilder로 변환해줍니다.
     * </p>
     * 그럼에도 불구하고 해당 메소드 내에서 StringBuilder를 사용한 이유는
     * 추후에 반복문이 사용될 가능성을 염두하여 StringBuilder를 타입으로 선택했습니다.
     *
     * @param discountType  할인 종류 (DiscountType enum 값)
     * @param discountRate  할인율 (percent 할인의 경우)
     * @param discountPrice 할인액 (amount 할인의 경우)
     * @return 할인 정보를 나타내는 문자열
     */
    public String getDiscountInfo(DiscountType discountType, Long discountRate, Long discountPrice) {
        StringBuilder discountMsg = new StringBuilder();
        if (discountType == DiscountType.AMOUNT) {
            return discountMsg.append(discountPrice).append("원 할인").toString();
        } else {
            return discountMsg.append(discountRate).append("% 할인").toString();
        }
    }
}
