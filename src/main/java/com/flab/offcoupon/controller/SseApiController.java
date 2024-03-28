package com.flab.offcoupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.offcoupon.domain.sse.SseConnectionPool;
import com.flab.offcoupon.domain.sse.UserSseConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/sse")
@RestController
public class SseApiController {

    private final SseConnectionPool sseConnectionPool;
    private final ObjectMapper objectMapper;
    @GetMapping(path = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseBodyEmitter connect (@RequestParam final long memberId) {

        UserSseConnection userSseConnection = UserSseConnection.connect(
                String.valueOf(memberId),
                sseConnectionPool,
                objectMapper
        );
        sseConnectionPool.addUniqueKey(String.valueOf(memberId), userSseConnection);
        return userSseConnection.getSseEmitter();

    }
}
