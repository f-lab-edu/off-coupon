package com.flab.offcoupon.model;

import com.flab.offcoupon.exception.common.NonPositiveValueException;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

import static com.flab.offcoupon.exception.common.NonPositiveValueException.MUST_BE_POSITIVE;

@Getter
public final class PositiveBigDecimal {

    private final BigDecimal value;

    public PositiveBigDecimal(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NonPositiveValueException(MUST_BE_POSITIVE);
        }
        this.value = value;
    }

    @Getter
    public static class Values {

        private final List<BigDecimal> values;

        public Values(List<BigDecimal> values) {
            if (values == null || values.stream().anyMatch(v -> v == null || v.compareTo(BigDecimal.ZERO) <= 0)) {
                throw new NonPositiveValueException(MUST_BE_POSITIVE);
            }
            this.values = values;
        }
    }
}
