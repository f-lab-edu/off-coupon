package com.flab.offcoupon.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Json Parsing Error")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonParsingException extends CustomException {
    public JsonParsingException(String message) {
        super(message);
    }
}
