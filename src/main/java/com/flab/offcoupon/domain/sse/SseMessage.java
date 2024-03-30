package com.flab.offcoupon.domain.sse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

/**
 * Redis 에서 발행된 메시지를 SseEmitter 로 전달하기 위한 메시지 객체입니다.
 * SseEmitter 로 전달할 때는 JSON 형태로 변환하여 전달합니다.
 */
@NoArgsConstructor
public class SseMessage {

    @JsonProperty
    private String comment;

    @JsonProperty
    private long memberId;

    SseMessage(String comment, long memberId) {
        this.comment = comment;
        this.memberId = memberId;
    }

    public static SseMessage generate(String comment, long memberId) {
        return new SseMessage(comment, memberId);
    }
}
