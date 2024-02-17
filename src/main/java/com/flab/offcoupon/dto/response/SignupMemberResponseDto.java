package com.flab.offcoupon.dto.response;

import com.flab.offcoupon.domain.Member;
import lombok.Generated;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Generated
@Getter
public final class SignupMemberResponseDto {

    private final String email;

    private final String name;

    private final LocalDate birthdate;

    private final String phone;

    private SignupMemberResponseDto(String email, String name, LocalDate birthdate, String phone) {
        this.email = email;
        this.name = name;
        this.birthdate = birthdate;
        this.phone = phone;
    }

    public static SignupMemberResponseDto create (Member member) {
        return  new SignupMemberResponseDto(member.getEmail(), member.getName(), member.getBirthdate(), member.getPhone());
    }
}
