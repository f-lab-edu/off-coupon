package com.flab.offcoupon.domain;

import com.flab.offcoupon.controller.api.MemberMapperDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

@Generated
@Getter
@ToString
public class Member {

    @Id
    private long id;

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

    private Member(MemberMapperDTO memberMapperDTO) {
        this.email = memberMapperDTO.getEmail();
        this.password = memberMapperDTO.getPassword();
        this.name = memberMapperDTO.getName();
        this.birthDate = memberMapperDTO.getBirthDate();
        this.phone = memberMapperDTO.getPhone();
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

    public static Member toEntity(MemberMapperDTO memberMapperDTO) {
        return new Member(memberMapperDTO);
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
