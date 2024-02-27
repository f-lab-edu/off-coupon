package com.flab.offcoupon.exception.event;

import com.flab.offcoupon.exception.CustomException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventNotFoundException extends CustomException {

    public EventNotFoundException(String message) {
        super(message);
    }
}
