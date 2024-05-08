package com.flab.offcoupon.dto.request;

import com.flab.offcoupon.exception.common.NonPositiveValueException;
import com.flab.offcoupon.model.Positive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

class IssueRequestParameterTest {
    @Nested
    @DisplayName("IssueRequestParameter 생성 테스트")
    class createIssueRequestParameter {
        @DisplayName("[SUCCESS] IssueRequestParameter 생성 성공")
        @ParameterizedTest
        @CsvSource(value = {"1, 2, 3", "2, 3, 4", "3, 4, 5"})
        void IssueRequestParameter(long eventId, long couponId, long memberId) {
            IssueRequestParameter issueRequestParameter =
                    new IssueRequestParameter(new Positive(eventId), new Positive(couponId), new Positive(memberId));
            assertEquals(eventId, issueRequestParameter.getEventId());
            assertEquals(couponId, issueRequestParameter.getCouponId());
            assertEquals(memberId, issueRequestParameter.getMemberId());
        }

        @DisplayName("[ERROR] IssueRequestParameter  객체 생성 실패 시 NonPositiveValueException 발생")
        @ParameterizedTest
        @CsvSource(value = {"-1, 2, 3", "2, -3, 4", "3, 4, -5", "-1, -2, -3"})
        void IssueRequestParameterTest(long eventId, long couponId, long memberId) {
            assertThrows(NonPositiveValueException.class, () -> {
                        new IssueRequestParameter(new Positive(eventId), new Positive(couponId), new Positive(memberId));
            });

        }
    }
}