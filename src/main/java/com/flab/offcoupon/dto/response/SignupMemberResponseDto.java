package com.flab.offcoupon.dto.response;

import com.flab.offcoupon.domain.Member;
import lombok.Generated;
import lombok.Getter;

@Generated
@Getter
public final class SignupMemberResponseDto {

    private final String email;


    private final String name;

    private final String birthdate;

    private final String phone;

    private SignupMemberResponseDto(String email, String name, String birthdate, String phone) {
        this.email = email;
        this.name = name;
        this.birthdate = birthdate;
        this.phone = phone;
    }

    public static SignupMemberResponseDto create (Member member) {
        return  new SignupMemberResponseDto(member.getEmail(), member.getName(), member.getBirthdate(), member.getBirthdate());
    }
}
