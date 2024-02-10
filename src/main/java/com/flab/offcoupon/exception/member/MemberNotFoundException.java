package com.flab.offcoupon.exception.member;

import com.flab.offcoupon.exception.CustomException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor (access = AccessLevel.PRIVATE)
public class MemberNotFoundException  extends CustomException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}
