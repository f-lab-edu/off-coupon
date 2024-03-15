package com.flab.offcoupon.domain.vo.persistence.couponissue;


import java.time.LocalDate;

/**
 * MyBatis에서 여러개의 파라미터를 전달하기 위한 VO
 * @param memberId
 * @param couponId
 * @param currentDateTime
 */
public record CouponIssueCheckVo(long memberId, long couponId, LocalDate currentDateTime) {
}
