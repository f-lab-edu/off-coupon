package com.flab.offcoupon.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import java.time.LocalDate;

@Getter
@ToString
public final class Member {

    @Id
    private long id;

    @NotBlank
    private final String email;

    @NotBlank
    private final String password;

    @NotBlank
    private final String name;

    @NotBlank
    private final String birthDate;

    @NotBlank
    private final String phone;

    @NotBlank
    private final LocalDate createdAt;

    @NotBlank
    private final LocalDate updatedAt;

    private Member(String email, String password, String name, String birthDate, String phone, LocalDate createdAt, LocalDate updatedAt) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Member create (String email, String password, String name, String birthDate, String phone) {
        LocalDate now = LocalDate.now();
        return new Member(email, password, name, birthDate, phone, now, now);
    }
}
