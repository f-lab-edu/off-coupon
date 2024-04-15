package com.flab.offcoupon.domain.entity.helper;

import com.flab.offcoupon.domain.entity.CouponStatus;
import com.flab.offcoupon.domain.entity.DiscountType;
import com.flab.offcoupon.domain.vo.persistence.order.AvailableCouponsByMemberIdVo;
import com.flab.offcoupon.util.DiscountUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * 사용 가능한 쿠폰 정보를 담는 클래스입니다.
 * 비즈니스 로직에서 데이터 가공을 위해 사용됩니다.
 */
@ToString
@Getter
@AllArgsConstructor
public final class AvailableCouponInfo {
    private final long couponId;
    private final String category;
    private final String description;
    private final BigDecimal minProductPrice; // 최소 주문 금액
    private final BigDecimal productPrice; // 상품 가격
    private final String discount; // 할인 쿠폰 내용
    private final BigDecimal discountPrice; // 상품 가격에 할인 쿠폰 적용한 할인가
    private final LocalDateTime validateStartDate;
    private final LocalDateTime validateEndDate;
    private final long couponIssueId;
    private final CouponStatus couponStatus;

    public AvailableCouponInfo(AvailableCouponsByMemberIdVo vo) {
        this.couponId = vo.couponId();
        this.category = vo.category();
        this.description = vo.description();
        this.minProductPrice = vo.minOrderPrice();
        this.productPrice = productPrice(vo);
        this.discount = DiscountUtils.getDiscountInfo(vo.discountType(), vo.discountRate(), vo.discountPrice());
        this.discountPrice = calculateDiscountPrice(vo);
        this.validateStartDate = vo.validateStartDate();
        this.validateEndDate = vo.validateEndDate();
        this.couponIssueId = vo.couponIssueId();
        this.couponStatus = vo.couponStatus();
    }

    /**
     * 상품의 salePrice가 0보다 큰지 확인합니다.
     *
     * @param salePrice 상품의 할인 가격
     * @return salePrice가 0보다 크면 true, 아니면 false
     */
    private boolean isSalePriceOverThanZero(BigDecimal salePrice) {
        return salePrice.compareTo(BigDecimal.ZERO) > 0;
    }

    private BigDecimal productPrice(AvailableCouponsByMemberIdVo vo) {
        if (isSalePriceOverThanZero(vo.salePrice())) {
            return vo.salePrice();
        }
        return vo.originalPrice();
    }

    /**
     * 할인 가격을 계산합니다.<br>
     * 상품 가격은 salePrice가 0보다 크면 salePrice를, salePrice가 0보다 작으면 originalPrice를 사용합니다.
     * 할인 가격은 할인 타입에 따라 다르게 계산됩니다.<br>
     * <li>할인 타입이 AMOUNT일 경우, 할인 가격은 discountPrice를 사용합니다.</li>
     * <li>할인 타입이 PERCENT일 경우 할인 가격은 price * discountRate / 100을 사용합니다.</li>
     *
     * @param vo 사용 가능한 쿠폰 정보
     * @return 할인 가격
     */
    public BigDecimal calculateDiscountPrice(AvailableCouponsByMemberIdVo vo) {
        long discountRate = vo.discountRate() == null ? 0 : vo.discountRate();
        long discountPrice = vo.discountPrice() == null ? 0 : vo.discountPrice();
        if (isSalePriceOverThanZero(vo.salePrice())) {
            return calculateDiscountPrice(vo.discountType(), vo.salePrice(), discountRate, discountPrice);
        } else {
            return calculateDiscountPrice(vo.discountType(), vo.originalPrice(), discountRate, discountPrice);
        }
    }

    /**
     * 할인 가격을 계산합니다.<br>
     *
     * @param discountType    할인 타입
     * @param price           상품 가격
     * @param discountRate    할인율
     * @param discountedPrice 할인액
     * @return
     */
    public BigDecimal calculateDiscountPrice(DiscountType discountType, BigDecimal price, long discountRate, long discountedPrice) {
        if (isDiscountTypePercent(discountType)) {
            // 할인율을 백분율로 변환하여 가격에서 할인 가격을 계산
            BigDecimal discountPercentage = BigDecimal.valueOf(discountRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            return price.multiply(discountPercentage).setScale(0, RoundingMode.DOWN); // 소수점 이하 자릿수를 제거하여 반환
        } else {
            return BigDecimal.valueOf(discountedPrice);
        }
    }

    private boolean isDiscountTypePercent(DiscountType discountType) {
        return discountType == DiscountType.PERCENT;
    }
}
