package com.flab.offcoupon.domain.vo.persistence.order;

import java.time.LocalDateTime;

/**
 * MyBatis에서 여러개의 파라미터를 전달하기 위한 VO
 *
 * @param memberId
 * @param productId
 * @param currentDateTime
 */
public record MemberIdProductIdNowVo(
        long memberId,
        long productId,
        LocalDateTime currentDateTime
) {
}

