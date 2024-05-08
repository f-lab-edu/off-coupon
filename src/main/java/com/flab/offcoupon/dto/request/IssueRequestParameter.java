package com.flab.offcoupon.dto.request;

import com.flab.offcoupon.model.Positive;
import lombok.Getter;

/**
 * 쿠폰 발급 요청 파라미터를 담는 DTO 클래스입니다.
 * <p>이 클래스는 쿠폰 발급 요청 시 필요한 이벤트 ID, 쿠폰 ID, 회원 ID를 담습니다.</p>
 * <p>이벤트 ID, 쿠폰 ID, 회원 ID는 모두 Positive 객체를 통해 음수가 아닌 값으로 제한합니다.</p>
 *
 */
@Getter
public final class IssueRequestParameter {

    private final long eventId;
    private final long couponId;
    private final long memberId;

    public IssueRequestParameter(Positive eventId, Positive couponId, Positive memberId) {
        this.eventId = eventId.getValue();
        this.couponId = couponId.getValue();
        this.memberId = memberId.getValue();
    }
}
