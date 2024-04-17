package com.flab.offcoupon.dto.response;

import com.flab.offcoupon.domain.entity.CouponStatus;
import com.flab.offcoupon.domain.vo.persistence.mypage.AllCouponsByMemberIdVo;
import com.flab.offcoupon.util.DiscountUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public final class AllCouponsByMemberIdResponse {
    private final long couponId;
    private final String category;
    private final String description;
    private final String discount;
    private final LocalDateTime validateStartDate;
    private final LocalDateTime validateEndDate;
    private final CouponStatus couponStatus;

    public AllCouponsByMemberIdResponse(AllCouponsByMemberIdVo vo) {
        this.couponId = vo.couponId();
        this.category = vo.category();
        this.description = vo.description();
        this.discount = DiscountUtils.getDiscountInfo(vo.discountType(), vo.discountRate(), vo.discountPrice());
        this.validateStartDate = vo.validateStartDate();
        this.validateEndDate = vo.validateEndDate();
        this.couponStatus = vo.couponStatus();
    }
}
