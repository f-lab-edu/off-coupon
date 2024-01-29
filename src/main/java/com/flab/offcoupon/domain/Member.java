package com.flab.offcoupon.domain;

import com.flab.offcoupon.controller.api.MemberMapperDTO;
import com.flab.offcoupon.util.bcrypt.BcryptPassword;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

@Data
public class Member {

    @NotNull
    @Id
    private int id;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String name;

    @NotNull
    private String birthDate;

    @NotNull
    private String phone;

    @NotNull
    private LocalDate createdAt;

    @NotNull
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
