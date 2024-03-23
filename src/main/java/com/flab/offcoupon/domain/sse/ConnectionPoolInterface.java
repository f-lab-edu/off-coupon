package com.flab.offcoupon.domain.sse;

public interface ConnectionPoolInterface<T, R> {

    void addSession(T key, R session);

    R getSession(T uniqueKey);
    void onCompletionCallback(R session);
}
