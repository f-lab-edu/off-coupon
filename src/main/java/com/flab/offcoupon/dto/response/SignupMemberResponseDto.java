package com.flab.offcoupon.dto.response;

import com.flab.offcoupon.domain.entity.Member;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Generated
@Getter
@RequiredArgsConstructor
public final class SignupMemberResponseDto {

    private final String email;

    private final String name;

    private final LocalDate birthdate;

    private final String phone;

    public SignupMemberResponseDto(Member entity) {
        this.email = entity.getEmail();
        this.name = entity.getName();
        this.birthdate = entity.getBirthdate();
        this.phone = entity.getPhone();
    }
}
