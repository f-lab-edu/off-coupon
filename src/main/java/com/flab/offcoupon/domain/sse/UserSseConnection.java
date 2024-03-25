package com.flab.offcoupon.domain.sse;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * 'new' 키워드로 각 사용자마다 생성되는 사용자별 서버-보낸 이벤트(SSE) 연결 객체를 나타냅니다.
 * 이 객체는 SSE Emitter 관련 기능을 캡슐화합니다.
 */
@EqualsAndHashCode
@ToString
@Getter
public final class UserSseConnection {

    private final String uniqueKey;

    private final SseEmitter sseEmitter;

    private final ConnectionPoolInterface<String, UserSseConnection> connectionPoolIfs;

    private final ObjectMapper objectMapper;

    /**
     * 새로운 UserSseConnection 객체를 생성합니다.
     *
     * @param uniqueKey           사용자 연결과 관련된 고유 식별자입니다.
     * @param connectionPoolIfs   연결 관리를 위한 커넥션 풀 인터페이스입니다.
     * @param objectMapper        데이터 객체를 JSON으로 변환하기 위한 ObjectMapper입니다.
     */
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
        // on Completion(종료시 connection pool remove)
        this.sseEmitter.onCompletion(() -> connectionPoolIfs.onCompletionCallback(this));
        // on Timeout
        this.sseEmitter.onTimeout(() -> this.sseEmitter.complete());

        // 연결 설정을 나타내는 'onopen' 메시지를 보냅니다.
        sendMessage("onopen", "connect");
    }

    /**
     * 새로운 UserSseConnection 객체를 생성하고 연결을 설정하는 팩토리 메서드입니다.
     *
     * @param uniqueKey           사용자 연결과 관련된 고유 식별자입니다.
     * @param connectionPoolIfs   연결 관리를 위한 커넥션 풀 인터페이스입니다.
     * @param objectMapper        데이터 객체를 JSON으로 변환하기 위한 ObjectMapper입니다.
     * @return                    새로운 UserSseConnection 객체입니다.
     */
    public static UserSseConnection connect(String uniqueKey,
                                            ConnectionPoolInterface<String,
                                                    UserSseConnection> connectionPoolIfs,
                                            ObjectMapper objectMapper) {
        return new UserSseConnection(uniqueKey, connectionPoolIfs, objectMapper);
    }

    /**
     * 주어진 이벤트 이름과 데이터 객체를 포함한 SSE 메시지를 전송합니다.
     *
     * @param eventName   SSE 이벤트의 이름입니다.
     * @param data        SSE 메시지의 일부로 전송할 데이터 객체입니다.
     */
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

    /**
     * 데이터 객체를 JSON으로 변환하여 SSE 메시지를 전송합니다.
     *
     * @param data   SSE 메시지의 일부로 전송할 데이터 객체입니다.
     */
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
