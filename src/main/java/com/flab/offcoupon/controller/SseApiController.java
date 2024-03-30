package com.flab.offcoupon.controller;

import com.flab.offcoupon.service.sse.SseApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/sse")
@RestController
public class SseApiController {

    private final SseApiService sseApiService;

    /**
     * 클라이언트가 SSE 연결을 요청할 때 호출되는 메서드입니다.
     * Redis 채널에 해당 사용자의 구독을 등록합니다.
     * SSE 연결된 상태에서 구독 중인 채널에 메시지가 발행되면 클라이언트에게 SSE알림을 전송합니다.
     * @param memberId 사용자의 고유 식별자입니다.
     * @return SseEmitter 객체입니다.
     */
    @GetMapping(path = "/connect", produces = "text/event-stream")
    public SseEmitter subscribeAndConnect(@RequestParam final long memberId) {
        return sseApiService.subscribeAndConnect(memberId);
    }
}
