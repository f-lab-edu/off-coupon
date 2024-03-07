package com.flab.offcoupon.util;

public class CouponRedisUtils {
    // 요청의 unique 함을 유지하고 발급 수량의 제어를 위해 사용
    public static String getIssueRequestKey(long couponId) {
        return "issue.request.couponId=%s".formatted(couponId);
    }
    // 쿠폰 발급 대기 큐에 적재할때 사용
    public static String getIssueRequestQueueKey() {
        return "issue.request.queue";
    }
}
