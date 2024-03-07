package com.flab.offcoupon.dto.request;

/**
 * Redis에 쿠폰 발급 대기 목록을 저장하기 위한 DTO
 * @param couponId
 * @param userId
 */
public record CouponIssueRequestForQueue (
        long couponId,
        long userId
){
}
