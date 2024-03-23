package com.flab.offcoupon.domain.sse;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * new 키워드로 사용자마다 생성되는 객체
 */
@EqualsAndHashCode
@ToString
@Getter
public final class UserSseConnection {

    private final String uniqueKey;

    private final SseEmitter sseEmitter;

    private final ConnectionPoolInterface<String, UserSseConnection> connectionPoolIfs;

    private final ObjectMapper objectMapper;

    // 생성자 메소드는 명시적으로 connect가 되었음을 알려주지 못합니다
    // 따라서 static메소드를 ..사용
    private UserSseConnection(String uniqueKey,
                              ConnectionPoolInterface<String, UserSseConnection> connectionPoolIfs,
                              ObjectMapper objectMapper) {
        // key 초기화
        this.uniqueKey = uniqueKey;
        // sse 초기화
        this.sseEmitter = new SseEmitter(1000L * 60); // re-connection timeout (ms) - 60초
        // callback 초기화
        this.connectionPoolIfs = connectionPoolIfs;
        // ojbectMapper 초기화
        this.objectMapper = objectMapper;
        // on Completion
        this.sseEmitter.onCompletion(() -> {
            // 클라이언트와 연결이 종료되었을 때 처리 connection pool remove
            connectionPoolIfs.onCompletionCallback(this);
        });
        // on Timeout
        this.sseEmitter.onTimeout(() -> {
            // 클라이언트와 Timeout 발생 시 completete
            this.sseEmitter.complete();
        });

        // onopen 메세지
        sendMessage("onopen", "connect");
    }

    public static UserSseConnection connect(String uniqueKey,
                                            ConnectionPoolInterface<String,
                                                    UserSseConnection> connectionPoolIfs,
                                            ObjectMapper objectMapper) {
        return new UserSseConnection(uniqueKey, connectionPoolIfs, objectMapper);
    }

    public void sendMessage(String eventName, Object data) {

        try {
            SseEmitter.SseEventBuilder event = SseEmitter.event()
                    .name(eventName)
                    .data(this.objectMapper.writeValueAsString(data));
            this.sseEmitter.send(event);
        } catch (IOException e) {
            this.sseEmitter.completeWithError(e);
        }
    }

    public void sendMessage(Object data) {
        try {
            var event = SseEmitter.event()
                    .data(this.objectMapper.writeValueAsString(data));
            this.sseEmitter.send(event);
        } catch (IOException e) {
            this.sseEmitter.completeWithError(e);
        }
    }
}
