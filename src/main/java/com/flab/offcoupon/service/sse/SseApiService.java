package com.flab.offcoupon.service.sse;

import com.flab.offcoupon.domain.sse.SseConnectionPool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor
@Service
public class SseApiService {

    private final SseConnectionPool sseConnectionPool;
    public SseEmitter subscribeAndConnect(long memberId) {
        return sseConnectionPool.subscribeAndConnect(memberId);
    }
}
