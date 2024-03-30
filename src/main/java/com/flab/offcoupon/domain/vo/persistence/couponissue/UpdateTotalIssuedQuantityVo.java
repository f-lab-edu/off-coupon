package com.flab.offcoupon.domain.vo.persistence.couponissue;

/**
 * MyBatis에서 여러개의 파라미터를 전달하기 위한 VO
 *
 * 비동기 쿠폰 발행 기능에서 총 발급된 쿠폰 수량을 일괄 업데이트하기 위해 사용
 * @param couponId
 * @param issuedQuantity
 */
public record UpdateTotalIssuedQuantityVo (
        long couponId,
        long issuedQuantity
){
}
