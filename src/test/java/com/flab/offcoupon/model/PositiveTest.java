package com.flab.offcoupon.model;

import com.flab.offcoupon.exception.common.NonPositiveValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class PositiveTest {

    @Nested
    @DisplayName("단일 value의 음수 검증 테스트")
    class positiveValue {

        @DisplayName("[SUCCESS] Positive 생성 성공")
        @ParameterizedTest
        @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7, 8, 9})
        void positiveTest(long value) {
            Positive positive = new Positive(value);
            assertEquals(value, positive.getValue());
        }

        @DisplayName("[ERROR] Positive 객체 생성 실패 시 NonPositiveValueException 발생")
        @ParameterizedTest
        @ValueSource(longs = {0, -1, -100, -1000})
        void nonPositiveTest(long value) {
            assertThrows(NonPositiveValueException.class, () -> {
                new Positive(value);
            });
        }
    }

    @Nested
    @DisplayName("여러 values의 음수 검증 테스트")
    class positiveValues {
        @DisplayName("[SUCCESS] Positive.Values 생성 성공")
        @ParameterizedTest
        @MethodSource("onlyPositiveValues")
        void positiveTest(List<Long> values) {
            Positive.Values positiveValues = new Positive.Values(values);
            assertEquals(values, positiveValues.getValues());
        }

        private static Stream<Arguments> onlyPositiveValues() {
            return Stream.of(
                    arguments(List.of(1L)),
                    arguments(List.of(1L, 2L, 3L, 4L, 5L)),
                    arguments(List.of(6L, 7L, 8L, 9L)),
                    arguments(List.of(100L, 200L, 300L, 400L, 500L))
            );
        }

        @DisplayName("[ERROR] Positive.Values 객체 생성 실패 시 NonPositiveValueException 발생")
        @ParameterizedTest
        @MethodSource("nonPositiveValues")
        void nonPositiveTest(List<Long> values) {
            assertThrows(NonPositiveValueException.class, () -> {
                new Positive.Values(values);
            });
        }

        private static Stream<Arguments> nonPositiveValues() {
            return Stream.of(
                    arguments(List.of(0L)),
                    arguments(List.of(-1L)),
                    arguments(List.of(-1L, 2L, 3L, 4L, 5L)) // 음수 포함된 경우
            );
        }
    }

}