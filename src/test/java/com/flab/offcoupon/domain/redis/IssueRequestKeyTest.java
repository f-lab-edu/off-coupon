package com.flab.offcoupon.domain.redis;

import com.flab.offcoupon.exception.common.NonPositiveValueException;
import com.flab.offcoupon.model.Positive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

class IssueRequestKeyTest {

    @DisplayName("[SUCCESS] IssueRequestKey 객체 생성")
    @ParameterizedTest
    @CsvSource(value = {"1, 1", "2, 2", "3, 3", "4, 4", "5, 5", "6, 6", "7, 7", "8, 8", "9, 9"})
    void issueRequestKey_ok(long value) {
        IssueRequestKey issueRequestKey = new IssueRequestKey(new Positive(value), new Positive(value));
        assertEquals(value, issueRequestKey.getCouponId());
        assertEquals(value, issueRequestKey.getMemberId());
    }

    @DisplayName("[ERROR] IssueRequestKey 객체 생성 실패 시 NonPositiveValueException 발생")
    @ParameterizedTest
    @CsvSource(value = {"0, 0", "-1, -1", "-100, -100", "-1000, -1000"})
    void issueRequestKey_fail(long value) {
        assertThrows(NonPositiveValueException.class, () -> {
            new IssueRequestKey(new Positive(value), new Positive(value));
        });
    }
}