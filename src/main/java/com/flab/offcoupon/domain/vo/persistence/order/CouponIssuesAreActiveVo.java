package com.flab.offcoupon.domain.vo.persistence.order;

/**
 * MyBatis에서 여러개의 반환 값을 전달 받기 위한 VO
 * 발급된 쿠폰들의 상태가 활성화인지 확인하기 위해 사용
 * @param couponIssueId 쿠폰 발급 ID
 * @param isActive 쿠폰의 상태가 활성화인지 여부
 */
public record CouponIssuesAreActiveVo(
        long couponIssueId,
        boolean isActive

) {
}
