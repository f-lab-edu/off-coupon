package com.flab.offcoupon.exception;

public abstract class CustomBadRequestException extends RuntimeException {

    public CustomBadRequestException(String message) {
        super(message);
    }
}
