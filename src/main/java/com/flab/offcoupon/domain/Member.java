package com.flab.offcoupon.domain;

import com.flab.offcoupon.controller.api.MemberMapperDTO;
import com.flab.offcoupon.util.bcrypt.BcryptPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

@Generated
@Getter
@ToString
public class Member {

    @Id
    private int id;

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String birthDate;

    @NotBlank
    private String phone;

    @NotBlank
    private LocalDate createdAt;

    @NotBlank
    private LocalDate updatedAt;

    @Builder
    private Member(String email, String password, String name, String birthDate, String phone) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
        this.phone = phone;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

    public static Member toEntity(MemberMapperDTO memberMapperDTO) {
        return Member.builder()
                .email(memberMapperDTO.getEmail())
                .password(BcryptPassword.encrypt(memberMapperDTO.getPassword()))
                .name(memberMapperDTO.getName())
                .birthDate(memberMapperDTO.getBirthDate())
                .phone(memberMapperDTO.getPhone())
                .build();
    }
}
