package com.flab.offcoupon.exception.member;

import com.flab.offcoupon.exception.CustomException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
@NoArgsConstructor (access = AccessLevel.PRIVATE)
public class MemberNotFoundException  extends CustomException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}
