package com.flab.offcoupon.component.sse;

import com.flab.offcoupon.domain.sse.SseConnectionPool;
import com.flab.offcoupon.domain.sse.UserSseConnection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * SSE(Servlet Server Events)를 통해 메시지를 전송하는 컴포넌트 클래스입니다.
 */
@RequiredArgsConstructor
@Component
public class SseAlertSender {

    private final SseConnectionPool sseConnectionPool;
    /**
     * 특정 회원에게 SSE 메시지를 전송합니다.
     *
     * @param memberId 회원 ID
     * @param message 전송할 메시지
     */
    public void pushSseMessage(Long memberId, String message) {
        UserSseConnection userSseConnection = sseConnectionPool.getSession(String.valueOf(memberId));
        Optional.ofNullable(userSseConnection)
                .ifPresent(it -> it.sendMessage(message.formatted(memberId)));
    }
}
