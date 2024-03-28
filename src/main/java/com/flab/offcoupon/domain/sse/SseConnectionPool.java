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
     * 각 사용자별 SseEmitter를 저장하는 맵
     *
     * SseEmitter는 클라이언트와 서버 간의 연결을 유지하고, 서버에서 클라이언트로 데이터를 전송할 수 있습니다.
     * 한 번 사용한 SseEmitter는 재사용할 수 없으므로, 사용자별로 연결을 관리해야 합니다.
     *
     * 각 사용자의 연결 정보를 요청이 있을 때마다 저장하고, 새로운 요청이 들어올 때 저장된 연결 정보를 재사용하여 데이터를 전송합니다.
     * 연결 정보를 저장, 검색, 전송하는 작업은 각기 다른 스레드에서 수행되므로 스레드 간 경합이 발생할 수 있습니다.
     * 이를 해결하기 위해 ConcurrentHashMap을 사용합니다.
     */
    private static final Map<String, UserSseConnection> connectionPool = new ConcurrentHashMap<>();

    /**
     * 새로운 사용자 식별자를 커넥션 풀에 추가합니다.
     *
     * @param uniqueKey             사용자의 고유 식별자입니다.
     * @param userSseConnection     사용자의 Sse 연결 객체입니다.
     */
    @Override
    public void addUniqueKey(String uniqueKey, UserSseConnection userSseConnection) {
        connectionPool.put(uniqueKey, userSseConnection);
    }

    /**
     * 주어진 고유 식별자에 해당하는 사용자 객체를 가져옵니다.
     *
     * @param uniqueKey             사용자의 고유 식별자입니다.
     * @return                      주어진 고유 식별자에 해당하는 사용자의 Sse 연결 객체입니다.
     * @throws IllegalArgumentException 주어진 고유 식별자에 해당하는 객체가 없을 때 발생합니다.
     */
    @Override
    public UserSseConnection getUniqueKey(String uniqueKey) {
        return Optional.of(connectionPool.get(String.valueOf(uniqueKey)))
                .orElseThrow(() -> new IllegalArgumentException("해당 식별자를 찾을 수 없습니다. memberId : " + uniqueKey));
    }
    /**
     * 사용자 식별자별 완료 콜백을 처리합니다.
     *
     * @param session   완료된 사용자 식별자입니다.
     */
    @Override
    public void onCompletionCallback(UserSseConnection session) {
        log.info("call back connection pool completion : {}", session);
        connectionPool.remove(session.getUniqueKey());
    }
}
