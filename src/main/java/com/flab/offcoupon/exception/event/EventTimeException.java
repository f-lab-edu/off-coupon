package com.flab.offcoupon.exception.event;

import com.flab.offcoupon.exception.CustomException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventTimeException extends CustomException {

    public EventTimeException(String message) {
        super(message);
    }
}
