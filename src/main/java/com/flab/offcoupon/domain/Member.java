package com.flab.offcoupon.domain;

import com.flab.offcoupon.controller.api.MemberMapperDTO;
import lombok.Builder;

public class Member {

    private String email;
    private String password;
    private String name;
    private String birthDate;
    private String phone;

    @Builder
    private Member(String email, String password, String name, String birthDate, String phone) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
        this.phone = phone;
    }

    public static Member toEntity(MemberMapperDTO memberMapperDTO) {
        return new MemberBuilder()
                .email(memberMapperDTO.getEmail())
                .password(memberMapperDTO.getPassword())
                .name(memberMapperDTO.getName())
                .birthDate(memberMapperDTO.getBirthDate())
                .phone(memberMapperDTO.getPhone())
                .build();
    }
}
