package com.flab.offcoupon.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
