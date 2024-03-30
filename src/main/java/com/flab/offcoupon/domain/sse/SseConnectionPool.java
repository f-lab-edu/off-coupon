package com.flab.offcoupon.domain.sse;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.flab.offcoupon.util.RedisKeyUtils.getUserSseConnectionKey;

/**
 * SseEmitter 객체를 관리하는 커넥션 풀입니다.
 */
@Getter
@Slf4j
@RequiredArgsConstructor
@Component
public class SseConnectionPool {
    private static final long SSE_CONNECTION_TIMEOUT = 1000L * 60;
    private final ObjectMapper objectMapper;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private static final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();


    /**
     * 새로운 사용자 식별자를 커넥션 풀에 추가하고, Redis 메세지 리스너로부터 메세지를 수신합니다.<br>
     * 메세지 리스너는 Redis Pub/Sub 구조의 Subscriber로서 메세지를 수신합니다.<br>
     * 구독한 Topic에 메세지가 발행되면 해당 메세지를 SseEmitter로 전송합니다.<br>
     * SseEmitter는 클라이언트와의 연결을 유지하며, 클라이언트에게 메세지를 전송합니다.<br>
     *
     * @param memberId 사용자의 고유 식별자입니다.
     * @return SseEmitter 객체입니다.
     * @See <a href="https://github.com/vpavic/samples-spring-sse-redis">Samples: Spring Server-Sent Events with Redis</a>
     */
    public SseEmitter subscribeAndConnect(long memberId) throws IOException {
        log.info("connect - memberId: {}", memberId);
        SseEmitter emitter = new SseEmitter(SSE_CONNECTION_TIMEOUT);
        // 초기 연결용 메세지
        emitter.send(SseEmitter.event().comment("connected"));
        emitters.add(emitter);
        // Redis 메시지 리스너 등록(Subscribe)
        MessageListener messageListener = (message, pattern) -> {
            try {
                emitter.send(SseEmitter.event().data(serialize(message)));
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        };
        this.redisMessageListenerContainer.addMessageListener(messageListener,
                ChannelTopic.of(getUserSseConnectionKey().formatted(memberId)));

        // 연결 timeout 시 처리
        emitter.onTimeout(() -> {
            log.info("Connection timeout - memberId: {}", memberId);
            emitter.onCompletion(() -> onCompletionCallback(emitter, messageListener));
        });
        // 연결 Error 발생 시 처리
        emitter.onError(throwable -> {
            emitter.onCompletion(() -> onCompletionCallback(emitter, messageListener));
        });
        return emitter;
    }

    /**
     * Redis 메시지를 SseMessage로 변환합니다.
     *
     * @param message Redis 메시지입니다.
     * @return SseMessage 객체입니다.
     */
    private SseMessage serialize(Message message) {
        try {
            return this.objectMapper.readValue(message.getBody(), SseMessage.class);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }


    /**
     * 사용자 식별자별 완료 콜백을 처리합니다.
     *
     * @param emitter ResponseBodyEmitter 객체입니다. 연결이 완료되면 커넥션 풀에서 제거합니다.
     * @Param messageListener MessageListener 객체입니다. Redis 메시지 리스너를 제거합니다.
     */
    public void onCompletionCallback(SseEmitter emitter, MessageListener messageListener) {
        log.info("Callback for connection completion");
        emitters.remove(emitter);
        this.redisMessageListenerContainer.removeMessageListener(messageListener);
    }
}