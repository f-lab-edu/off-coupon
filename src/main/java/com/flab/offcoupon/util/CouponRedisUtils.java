package com.flab.offcoupon.util;

import lombok.experimental.UtilityClass;

/**
 * Redis 에서 사용하는 key 를 생성하는 유틸리티 클래스
 */
@UtilityClass
public class CouponRedisUtils {
    private static final String ISSUE_REQUEST_WITH_COUPON_ID_PREFIX = "issue.request.couponId=%s";
    private static final String ISSUE_REQUEST_QUEUE_PREFIX = "issue.request.queue";
    // 요청의 unique 함을 유지하고 발급 수량의 제어를 위해 사용
    public static String getIssueRequestKey(long couponId) {
        return ISSUE_REQUEST_WITH_COUPON_ID_PREFIX.formatted(couponId);
    }
}
