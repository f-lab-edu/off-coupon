package com.flab.offcoupon.domain.entity;

public enum CouponStatus {
    NOT_ACTIVE("유효기간 시작 전"),
    ACTIVE("유효 기간 중"),
    USED("사용 완료"),
    EXPIRED("기간 완료");

    private final String description;

    CouponStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
