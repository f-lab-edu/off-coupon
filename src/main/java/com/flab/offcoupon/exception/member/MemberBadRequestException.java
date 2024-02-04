package com.flab.offcoupon.exception.member;

import com.flab.offcoupon.exception.CustomException;
import lombok.NoArgsConstructor;

@NoArgsConstructor()
public class MemberBadRequestException extends CustomException {
    public MemberBadRequestException(String message) {
        super(message);
    }
}
