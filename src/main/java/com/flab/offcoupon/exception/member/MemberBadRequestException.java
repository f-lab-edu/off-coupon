package com.flab.offcoupon.exception.member;

import com.flab.offcoupon.exception.CustomException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor (access = AccessLevel.PRIVATE)
public final class MemberBadRequestException extends CustomException {
    public MemberBadRequestException(String message) {
        super(message);
    }
}
