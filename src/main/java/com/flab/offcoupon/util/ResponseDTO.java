package com.flab.offcoupon.util;

import lombok.Getter;

@Getter
public class ResponseDTO {

    private final String message;
    private final Object result;

    public ResponseDTO(String message, Object result) {
        this.message = message;
        this.result = result;
    }
}
