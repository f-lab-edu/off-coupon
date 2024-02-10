package com.flab.offcoupon.exception.member;

import com.flab.offcoupon.exception.CustomException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor (access = AccessLevel.PRIVATE)
public class PasswordNotMatchException extends CustomException {
    public PasswordNotMatchException(String message) {
        super(message);

    }
}
