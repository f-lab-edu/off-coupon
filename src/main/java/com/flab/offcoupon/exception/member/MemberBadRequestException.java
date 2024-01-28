package com.flab.offcoupon.exception.member;

import com.flab.offcoupon.exception.CustomBadRequestException;

public class MemberBadRequestException extends CustomBadRequestException {
    public MemberBadRequestException(String message) {
        super(message);
    }
}
