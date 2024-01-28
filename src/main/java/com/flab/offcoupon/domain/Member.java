package com.flab.offcoupon.domain;

import com.flab.offcoupon.controller.api.MemberMapperDTO;
import com.flab.offcoupon.util.bcrypt.BcryptPassword;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

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
    private LocalDateTime createdAt;
    @NotNull
    private LocalDateTime updatedAt;

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
                .password(BcryptPassword.encrypt(memberMapperDTO.getPassword()))
                .name(memberMapperDTO.getName())
                .birthDate(memberMapperDTO.getBirthDate())
                .phone(memberMapperDTO.getPhone())
                .build();
    }
}
