package com.flab.offcoupon.model;

import com.flab.offcoupon.exception.common.NonPositiveValueException;
import lombok.Getter;

import java.util.List;

@Getter
public final class PositiveLong {

    private final long value;

    public PositiveLong(long value) {

        if (value <= 0) {
            throw new NonPositiveValueException();
        }
        this.value = value;
    }
    @Getter
    public static class Values {

        private final List<Long> values;

        public Values(List<Long> values) {
            if (values.stream().anyMatch(v -> v <= 0)) {
                throw new NonPositiveValueException();
            }
            this.values = values;
        }
    }
}
