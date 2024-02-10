package com.flab.offcoupon.dto.response;

import com.flab.offcoupon.domain.Member;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public final class LoginMemberResponseDto {

    private final long id;

    private final String email;

    private final String name;

    private final String birthdate;

    private final String phone;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    private LoginMemberResponseDto(long id, String email, String name, String birthdate, String phone, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.birthdate = birthdate;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static LoginMemberResponseDto create(Member member) {
        return new LoginMemberResponseDto(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getBirthdate(),
                member.getPhone(),
                member.getCreatedAt(),
                member.getUpdatedAt());
    }
}
