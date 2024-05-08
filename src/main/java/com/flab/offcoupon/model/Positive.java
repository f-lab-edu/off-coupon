package com.flab.offcoupon.model;

import com.flab.offcoupon.exception.common.NonPositiveValueException;
import lombok.Getter;

@Getter
public final class Positive {

    private final long value;

    public Positive(long value) {
        if (value <= 0) {
            throw new NonPositiveValueException();
        }
        this.value = value;
    }
}
