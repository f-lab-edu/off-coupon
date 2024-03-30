package com.flab.offcoupon.dto.request.rabbit_mq;

/**
 * 쿠폰 발급 대기 목록을 저장하기 위한 Message로서 RabbitMQ의 Queue에 적재됩니다.
 * @param couponId
 * @param memberId
 */
public record CouponIssueMessageForQueue(
        long couponId,
        long memberId
){
}
