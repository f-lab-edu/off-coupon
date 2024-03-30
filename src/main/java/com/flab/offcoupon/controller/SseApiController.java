package com.flab.offcoupon.controller;

import com.flab.offcoupon.component.redis.RedisPublisher;
import com.flab.offcoupon.service.sse.SseApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/sse")
@RestController
public class SseApiController {

    private final RedisPublisher redisPublisher;
    private final SseApiService sseApiService;

    /**
     * 클라이언트가 SSE 연결을 요청할 때 호출되는 메서드입니다.
     * Redis 채널에 해당 사용자의 구독을 등록합니다.
     * 그리고 서버에서 이벤트가 발생하면 해당 이벤트를 Redis 채널에 발행합니다.
     *
     * @param memberId
     * @return
     */
    @GetMapping(path = "/connect", produces = "text/event-stream")
    public SseEmitter subscribeAndConnect(@RequestParam final long memberId) throws IOException {
        return sseApiService.subscribeAndConnect(memberId);
    }

    @GetMapping("/test")
    public void test(@RequestParam final long memberId) {
        redisPublisher.publish("users:sse:publish",
                "쿠폰이 발급 완료되었습니다. memberId : %s".formatted(memberId));
    }
}
