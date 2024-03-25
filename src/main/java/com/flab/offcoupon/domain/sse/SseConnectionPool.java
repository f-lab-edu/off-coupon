package com.flab.offcoupon.domain.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SseConnectionPool implements ConnectionPoolInterface<String, UserSseConnection> {
    /**
     * 사용자별 SseEmitter를 저장할 Map
     *
     * SseEmitter는 클라이언트와 서버 간의 연결을 유지하며, 서버에서 클라이언트로 데이터를 전송할 수 있습니다.
     * SseEmitter는 한 번 사용하면 더 이상 사용할 수 없으므로, 사용자별로 연결을 관리해야 합니다.
     *
     * 사용자별로 연결을 관리하기 위해 요청이 들어올 때마다 해당 사용자의 연결 정보를 저장하고,
     * 새로운 요청이 들어올 때 저장된 연결 정보를 재사용하여 데이터를 발송합니다.
     * 각각 다른 스레드에서 연결 정보를 저장, 꺼내기, 발송하기 작업이 수행됩니다.
     * 이로 인해 스레드 간의 경합이 발생할 수 있으며, 이를 해결하기 위해 ConcurrentHashMap을 활용했습니다.
     */
    private static final Map<String, UserSseConnection> connectionPool = new ConcurrentHashMap<>();


    @Override
    public void addSession(String uniqueKey, UserSseConnection userSseConnection) {
        connectionPool.put(uniqueKey, userSseConnection);
    }

    @Override
    public UserSseConnection getSession(String uniqueKey) {
        return Optional.of(connectionPool.get(String.valueOf(uniqueKey)))
                .orElseThrow(() -> new IllegalArgumentException("해당 세션을 찾을 수 없습니다. session : " + uniqueKey));
    }

    @Override
    public void onCompletionCallback(UserSseConnection session) {
        log.info("call back connection pool completion : {}", session);
        connectionPool.remove(session.getUniqueKey());
    }
}
