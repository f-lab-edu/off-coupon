package com.flab.offcoupon.exception.statistics;

import com.flab.offcoupon.exception.CustomException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.BAD_REQUEST)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalDateBadRequestException extends CustomException {

    public LocalDateBadRequestException(String message) {
        super(message);
    }

}
