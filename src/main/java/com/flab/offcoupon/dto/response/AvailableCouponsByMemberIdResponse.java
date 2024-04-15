package com.flab.offcoupon.dto.response;

import com.flab.offcoupon.domain.entity.CouponStatus;
import com.flab.offcoupon.domain.entity.helper.AvailableCouponInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public final class AvailableCouponsByMemberIdResponse {
    private final long couponId;
    private final String category;
    private final String description;
    private final String discount; // 할인 쿠폰 내용
    private final BigDecimal discountPrice; // 상품 가격에 할인 쿠폰 적용한 할인가
    private final LocalDateTime validateStartDate;
    private final LocalDateTime validateEndDate;
    private final long couponIssueId;
    private final CouponStatus couponStatus;


    public AvailableCouponsByMemberIdResponse(AvailableCouponInfo info) {
        this.couponId = info.getCouponId();
        this.category = info.getCategory();
        this.description = info.getDescription();
        this.discount = info.getDiscount();
        this.discountPrice = info.getDiscountPrice();
        this.validateStartDate = info.getValidateStartDate();
        this.validateEndDate = info.getValidateEndDate();
        this.couponIssueId = info.getCouponIssueId();
        this.couponStatus = info.getCouponStatus();
    }

}
