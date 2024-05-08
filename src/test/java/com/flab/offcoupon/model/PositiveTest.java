package com.flab.offcoupon.model;

import com.flab.offcoupon.exception.common.NonPositiveValueException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PositiveTest {

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7, 8, 9})
    void positiveTest(long value) {
        Positive positive = new Positive(value);
        assertEquals(value, positive.getValue());
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, -100, -1000})
    void nonPositiveTest(long value) {
        assertThrows(NonPositiveValueException.class, () -> {
            new Positive(value);
        });

    }
}