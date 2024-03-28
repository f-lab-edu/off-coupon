package com.flab.offcoupon.domain.sse;

/**
 * 커넥션 풀 관리를 위한 인터페이스입니다.
 *
 * @param <T> 키의 타입입니다.
 * @param <R> 세션의 타입입니다.
 */
public interface ConnectionPoolInterface<T, R> {

    void addUniqueKey(T key, R session);
    R getUniqueKey(T uniqueKey);
    void onCompletionCallback(R session);
}
