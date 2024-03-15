package com.flab.offcoupon.domain.entity;

public enum CouponType {

    FIRST_COME_FIRST_SERVED("선착순"),
    RANDOM_DRAW("랜덤 추첨"),
    SIGN_UP("회원가입");

    private final String description;

    CouponType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
