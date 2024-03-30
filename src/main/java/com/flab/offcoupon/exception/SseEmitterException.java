package com.flab.offcoupon.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "SseEmitter Error")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SseEmitterException extends CustomException {
    public SseEmitterException(String message) {
        super(message);
    }
}
